package pe.seek.core.customer.infrastructure.adapter.broker.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pe.seek.core.customer.application.port.input.CustomerServicePort;
import pe.seek.core.customer.infrastructure.adapter.broker.events.CreatedCustomerEvent;
import pe.seek.core.customer.infrastructure.mapper.CustomerMapper;
import reactor.core.publisher.Mono;

import static pe.seek.core.shared.constants.TopicConstant.CREATE_CUSTOMER_BY_FALLBACK;

@Slf4j
@Component
@Profile({"dev"})
@RequiredArgsConstructor
class KafkaSubscriber {

    private final CustomerMapper customerMapper;
    private final CustomerServicePort customerService;

    @KafkaListener(topics = CREATE_CUSTOMER_BY_FALLBACK)
    public void createCustomerByFallBack(ConsumerRecord<String, CreatedCustomerEvent> consumerRecord){
        log.info("Received message with key: {} and value: {}", consumerRecord.key(), consumerRecord.value());
        Mono.just(consumerRecord.value())
                .flatMap(payload -> {
                    try {
                        return customerService.createCustomer(
                                customerMapper.toDomainFromEventFallBack(payload)
                        );
                    } catch (Exception e) {
                        log.error("Error processing fallback event: {}", e.getMessage(), e);
                        return Mono.error(e);
                    }
                })
                .doOnSuccess(c -> log.info("Successfully created fallback customer: {}", c))
                .doOnError(e -> log.error("Failed to create customer from fallback", e))
                .subscribe();
    }

}
