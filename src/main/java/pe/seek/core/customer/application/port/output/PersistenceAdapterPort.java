package pe.seek.core.customer.application.port.output;

import pe.seek.core.customer.domain.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersistenceAdapterPort {
    Mono<Customer> saveCustomer(Customer customer);
    Mono<Customer> getCustomerByPhone(String phone);
    Flux<Customer> getAllCustomers();
    Mono<Customer> updateCustomer(String phone, Customer customer);
    Mono<Void> deleteCustomer(String phone);
}
