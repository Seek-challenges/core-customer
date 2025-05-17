package pe.seek.core.customer.infrastructure.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends R2dbcRepository<CustomerEntity, String> {
    Mono<CustomerEntity> findByPhone(String phone);
    Mono<Void> deleteByPhone(String phone);
}
