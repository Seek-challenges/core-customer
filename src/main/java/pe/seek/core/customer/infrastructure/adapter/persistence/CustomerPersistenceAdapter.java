package pe.seek.core.customer.infrastructure.adapter.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import pe.seek.core.customer.application.port.output.PersistenceAdapterPort;
import pe.seek.core.customer.domain.Customer;
import pe.seek.core.customer.infrastructure.mapper.CustomerMapper;
import pe.seek.core.customer.infrastructure.repository.CustomerEntity;
import pe.seek.core.customer.infrastructure.repository.CustomerRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
class CustomerPersistenceAdapter implements PersistenceAdapterPort {

    private final CustomerMapper customerMapper;
    private final CustomerRepository repository;
    private final ReactiveRedisTemplate<String, Customer> redisTemplate;

    private static final String CACHE_PREFIX = "customer:";

    @Override
    public Mono<Customer> saveCustomer(Customer customer) {
        log.info("Saving customer: {}", customer.getPhone());
        CustomerEntity entity = customerMapper.toEntityFromDomain(customer);
        return repository.save(entity)
                .map(customerMapper::toDomainFromEntity)
                .flatMap(this::saveCachedCustomer)
                .doOnError(e -> log.error("Error saving customer: {}", e.getMessage(), e));
    }

    @Override
    public Mono<Customer> getCustomerByPhone(String phone) {
        String cacheKey = CACHE_PREFIX + phone;
        return redisTemplate.opsForValue().get(cacheKey)
                .doOnNext(cached -> log.info("Cache hit for customer: {}", phone))
                .switchIfEmpty(Mono.defer(() ->
                        repository.findByPhone(phone)
                                .map(customerMapper::toDomainFromEntity)
                                .flatMap(this::saveCachedCustomer)
                ))
                .doOnError(e -> log.error("Error retrieving customer by phone: {}", e.getMessage(), e));
    }

    @Override
    public Flux<Customer> getAllCustomers() {
        log.info("Retrieving all customers");
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
                .flatMap(this::saveCachedCustomer)
                .doOnError(e -> log.error("Error updating customer: {}", e.getMessage(), e));

    }

    @Override
    public Mono<Void> deleteCustomer(String phone) {
        String cacheKey = CACHE_PREFIX + phone;
        return repository.deleteByPhone(phone)
                .then(redisTemplate.delete(cacheKey)
                        .doOnSuccess(e -> log.info("Customer deleted from Redis cache: {}", phone))
                        .doOnError(e -> log.error("Error deleting customer from Redis cache: {}", e.getMessage(), e))
                )
                .then()
                .doOnError(e -> log.error("Error deleting customer: {}", e.getMessage(), e));
    }

    private Mono<Customer> saveCachedCustomer(Customer customer) {
        return redisTemplate.opsForValue()
                .set(CACHE_PREFIX + customer.getPhone(), customer)
                .then(Mono.just(customer))
                .doOnSuccess(cached -> log.info("Customer saved to Redis cache: {}", cached.getPhone()))
                .doOnError(e -> log.error("Error saving customer to Redis cache: {}", e.getMessage(), e));
    }
}
