package pe.seek.core.customer.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.seek.core.customer.application.port.input.CustomerServicePort;
import pe.seek.core.customer.application.port.output.CustomerCreatedPublisherPort;
import pe.seek.core.customer.application.port.output.PersistenceAdapterPort;
import pe.seek.core.customer.domain.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
class CustomerService implements CustomerServicePort {

    private final PersistenceAdapterPort persistenceAdapter;
    private final CustomerCreatedPublisherPort customerCreatedPublisher;

    @Override
    public Mono<Customer> createCustomer(Customer customer) {
        return persistenceAdapter.saveCustomer(customer)
                .doOnSuccess(customerCreatedPublisher::notifyCreatedCustomer)
                .doOnError(e -> log.error("Error creating customer: {}", e.getMessage(), e));
    }

    @Override
    public Mono<Customer> getCustomerByPhone(String phone) {
        return persistenceAdapter.getCustomerByPhone(phone)
                .doOnSuccess(customer -> log.info("Customer found: {}", customer.getPhone()))
                .doOnError(e -> log.error("Error retrieving customer: {}", e.getMessage(), e));
    }

    @Override
    public Flux<Customer> getAllCustomers() {
        return persistenceAdapter.getAllCustomers()
                .doOnComplete(() -> log.info("All customers retrieved successfully"))
                .doOnError(e -> log.error("Error retrieving all customers: {}", e.getMessage(), e));
    }

    @Override
    public Mono<Customer> updateCustomer(String phone, Customer customer) {
        return persistenceAdapter.updateCustomer(phone, customer)
                .doOnSuccess(updatedCustomer -> log.info("Customer updated successfully: {}", updatedCustomer.getPhone()))
                .doOnError(e -> log.error("Error updating customer: {}", e.getMessage(), e));
    }

    @Override
    public Mono<Void> deleteCustomer(String phone) {
        return persistenceAdapter.deleteCustomer(phone)
                .doOnSuccess(aVoid -> log.info("Customer deleted successfully: {}", phone))
                .doOnError(e -> log.error("Error deleting customer: {}", e.getMessage(), e));
    }
}
