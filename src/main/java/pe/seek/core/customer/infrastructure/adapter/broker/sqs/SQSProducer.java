package pe.seek.core.customer.infrastructure.adapter.broker.sqs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pe.seek.core.customer.application.port.output.CustomerCreatedPublisherPort;
import pe.seek.core.customer.domain.Customer;

@Slf4j
@Component
@Profile({"prod"})
@RequiredArgsConstructor
class SQSProducer implements CustomerCreatedPublisherPort {
    @Override
    public void notifyCreatedCustomer(Customer customer) {

    }
}
