package pe.seek.core.customer.application.port.input;

import pe.seek.core.customer.domain.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerServicePort {
    Mono<Customer> createCustomer(Customer customer);
    Mono<Customer> getCustomerByPhone(String phone);
    Flux<Customer> getAllCustomers();
    Mono<Customer> updateCustomer(String phone, Customer customer);
    Mono<Void> deleteCustomer(String phone);
}
