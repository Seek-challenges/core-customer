package pe.seek.core.customer.infrastructure.adapter.broker.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pe.seek.core.customer.application.port.output.CustomerCreatedPublisherPort;
import pe.seek.core.customer.domain.Customer;
import pe.seek.core.customer.infrastructure.adapter.broker.events.NotifiedCreateCustomer;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Slf4j
@Component
@Profile({"prod"})
@RequiredArgsConstructor
class SQSProducer implements CustomerCreatedPublisherPort {

    private final SqsAsyncClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.cloud.sqs.publisher.create-customer-notification}")
    private String createCustomerNotificationQueueUrl;

    @Override
    public void notifyCreatedCustomer(Customer customer) {
        try {
            log.info("Sending message notification to customer: {}", customer.getPhone());

            NotifiedCreateCustomer notification = NotifiedCreateCustomer.builder()
                    .fullName(customer.getFirstName() + " " + customer.getLastName())
                    .phone(customer.getPhone().toString())
                    .build();

            String jsonBody = objectMapper.writeValueAsString(notification);

            sqsClient.sendMessage(b -> b
                            .queueUrl(createCustomerNotificationQueueUrl)
                            .messageBody(jsonBody)
                    )
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send message to SQS queue", exception);
                        } else {
                            log.info("Message sent to SQS queue successfully: {}", result.messageId());
                        }
                    });

        } catch (Exception e) {
            log.error("Error serializing notification", e);
        }
    }
}
