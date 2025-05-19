package pe.seek.core.customer.infrastructure.adapter.broker.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pe.seek.core.customer.application.port.input.CustomerServicePort;
import pe.seek.core.customer.infrastructure.adapter.broker.events.CreatedCustomerEvent;
import pe.seek.core.customer.infrastructure.mapper.CustomerMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@Profile({"prod"})
@RequiredArgsConstructor
class SQSSubscriber {

    private final SqsAsyncClient sqsClient;
    private final ObjectMapper objectMapper;
    private final CustomerMapper customerMapper;
    private final CustomerServicePort customerService;

    @Value("${aws.cloud.sqs.consumer.create-customer-fallback}")
    private String s3QueueUrl;

    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Scheduled(fixedDelay = 5000)
    public void pollMessages() {
        if (shuttingDown.get()) {
            log.info("🚫 Polling detenido: app en shutdown.");
            return;
        }

        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(s3QueueUrl)
                .maxNumberOfMessages(5)
                .waitTimeSeconds(10)
                .build();

        Mono.fromFuture(() -> sqsClient.receiveMessage(receiveRequest))
                .flatMapMany(response -> Flux.fromIterable(response.messages()))
                .flatMap(this::handleMessage)
                .onErrorContinue((ex, o) -> log.error("❌ Error general en la recepción/procesamiento de mensajes", ex))
                .subscribe();
    }

    private Mono<Void> handleMessage(Message msg) {
        log.info("📥 Raw message: {}", msg.body());

        return Mono.fromCallable(() -> objectMapper.readValue(msg.body(), CreatedCustomerEvent.class))
                .flatMap(customer -> {
                    log.info("📌 Procesando cliente: {}", customer.phone());
                    return customerService.createCustomer(customerMapper.toDomainFromEventFallBack(customer))
                            .doOnSuccess(c -> log.info("✅ Cliente creado exitosamente: {}", c))
                            .doOnError(e -> log.error("❌ Error al crear cliente desde el evento", e));
                })
                .then(deleteMessage(msg))
                .onErrorResume(e -> {
                    log.error("❌ Error en deserialización o procesamiento del mensaje", e);
                    return Mono.empty();
                });
    }

    private Mono<Void> deleteMessage(Message message) {
        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(s3QueueUrl)
                .receiptHandle(message.receiptHandle())
                .build();

        return Mono.fromFuture(() -> sqsClient.deleteMessage(deleteRequest))
                .doOnSuccess(resp -> log.info("🧹 Mensaje borrado exitosamente: {}", message.messageId()))
                .doOnError(ex -> log.error("❌ Error al borrar mensaje", ex))
                .then();
    }

    @PreDestroy
    public void shutdown() {
        log.info("🧵 Marcando apagado del SQSSubscriber");
        shuttingDown.set(true);
    }
}
