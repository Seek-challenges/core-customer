package pe.seek.core.customer.infrastructure.adapter.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.seek.core.customer.application.port.output.PersistenceAdapterPort;
import pe.seek.core.customer.domain.Customer;
import pe.seek.core.customer.infrastructure.mapper.CustomerMapper;
import pe.seek.core.customer.infrastructure.repository.CustomerEntity;
import pe.seek.core.customer.infrastructure.repository.CustomerRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
class CustomerPersistenceAdapter implements PersistenceAdapterPort {

    private final CustomerMapper customerMapper;
    private final CustomerRepository repository;

    @Override
    public Mono<Customer> saveCustomer(Customer customer) {
        CustomerEntity entity = customerMapper.toEntityFromDomain(customer);
        return repository.save(entity)
                .map(customerMapper::toDomainFromEntity)
                .doOnError(e -> log.error("Error saving customer: {}", e.getMessage(), e));
    }

    @Override
    public Mono<Customer> getCustomerByPhone(String phone) {
        return repository.findByPhone(phone)
                .map(customerMapper::toDomainFromEntity)
                .doOnError(e -> log.error("Error retrieving customer by phone: {}", e.getMessage(), e));
    }

    @Override
    public Flux<Customer> getAllCustomers() {
        return repository.findAll()
                .map(customerMapper::toDomainFromEntity)
                .doOnError(e -> log.error("Error retrieving all customers: {}", e.getMessage(), e));
    }

    @Override
    public Mono<Customer> updateCustomer(String phone, Customer customer) {
        return repository.findByPhone(phone)
                .flatMap(existingCustomer -> {
                    CustomerEntity updatedEntity = customerMapper.toEntityFromDomain(customer);
                    return repository.save(existingCustomer.copyFrom(updatedEntity))
                            .map(customerMapper::toDomainFromEntity);
                })
                .doOnError(e -> log.error("Error updating customer: {}", e.getMessage(), e));

    }

    @Override
    public Mono<Void> deleteCustomer(String phone) {
        return repository.deleteByPhone(phone)
                .doOnError(e -> log.error("Error deleting customer: {}", e.getMessage(), e));
    }
}
