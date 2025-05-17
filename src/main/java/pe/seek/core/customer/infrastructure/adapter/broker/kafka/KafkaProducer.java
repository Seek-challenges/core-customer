package pe.seek.core.customer.infrastructure.adapter.broker.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pe.seek.core.customer.application.port.output.CustomerCreatedPublisherPort;
import pe.seek.core.customer.domain.Customer;
import pe.seek.core.customer.infrastructure.adapter.broker.events.NotifiedCreateCustomer;
import reactor.core.publisher.Mono;

import static pe.seek.core.shared.constants.TopicConstant.CREATE_CUSTOMER_NOTIFICATION;

@Slf4j
@Component
@Profile({"dev"})
@RequiredArgsConstructor
class KafkaProducer implements CustomerCreatedPublisherPort {

    private final KafkaTemplate<String, NotifiedCreateCustomer> kafkaTemplate;

    @Override
    public void notifyCreatedCustomer(Customer customer) {
        NotifiedCreateCustomer notification = NotifiedCreateCustomer.builder()
                .fullName(customer.getFirstName() + " " + customer.getLastName())
                .phone(customer.getPhone().toString())
                .build();

        Mono.fromFuture(kafkaTemplate.send(CREATE_CUSTOMER_NOTIFICATION, notification))
                .doOnSuccess(result -> log.info("Customer creation event published: {}", result.getRecordMetadata()))
                .doOnError(e -> log.error("Error publishing customer creation event", e))
                .subscribe();
    }
}
