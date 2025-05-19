package pe.seek.core.customer.infrastructure.adapter.router.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pe.seek.core.customer.application.port.input.CustomerServicePort;
import pe.seek.core.customer.domain.Customer;
import pe.seek.core.customer.infrastructure.adapter.router.handler.dto.request.CustomerRequestDTO;
import pe.seek.core.customer.infrastructure.adapter.router.handler.dto.request.group.CreatedCustomerGroup;
import pe.seek.core.customer.infrastructure.adapter.router.handler.dto.response.CustomerResponseDTO;
import pe.seek.core.customer.infrastructure.mapper.CustomerMapper;
import pe.seek.core.shared.validator.HandlerValidator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
class CustomerHandler implements CustomerHandlerPort{

    private final CustomerMapper customerMapper;
    private final HandlerValidator handlerValidator;
    private final CustomerServicePort customerServicePort;

    @Override
    public Mono<ServerResponse> createCustomer(ServerRequest request) {
        return request.bodyToMono(CustomerRequestDTO.class)
                .flatMap(dto -> handlerValidator.validate(dto, CreatedCustomerGroup.class)
                        .flatMap(validDto -> customerServicePort.createCustomer(
                                customerMapper.toDomainFromCreatedRequest(validDto))
                        )
                )
                .doOnSuccess(customerDomain -> log.info("Customer created successfully: {}", customerDomain.getPhone()))
                .flatMap(customerDomain -> ServerResponse
                        .created(request.uri())
                        .bodyValue(customerMapper.toResponseDTOFromDomain(customerDomain)))
                .onErrorResume(e -> {
                    log.error("Error creating customer: {}", e.getMessage());
                    return ServerResponse.badRequest().bodyValue(e.getMessage());
                });
    }

    @Override
    public Mono<ServerResponse> getCustomerByPhone(ServerRequest request) {
        String phone = request.pathVariable("phone");
        return customerServicePort.getCustomerByPhone(phone)
                .doOnSuccess(customerDomain -> log.info("Customer found: {}", customerDomain.getPhone()))
                .flatMap(customerDomain -> ServerResponse
                        .ok()
                        .bodyValue(customerMapper.toResponseDTOFromDomain(customerDomain)))
                .onErrorResume(e -> {
                    log.error("Error retrieving customer: {}", e.getMessage());
                    return ServerResponse.notFound().build();
                });
    }

    @Override
    public Mono<ServerResponse> getAllCustomers(ServerRequest request) {
        return customerServicePort.getAllCustomers()
                .map(customerMapper::toResponseDTOFromDomain)
                .collectList()
                .doOnSuccess(customers -> log.info("Customers retrieved successfully: {}", customers.size()))
                .flatMap(list -> ServerResponse.ok().bodyValue(list))
                .onErrorResume(e -> {
                    log.error("Error retrieving customers: {}", e.getMessage());
                    return ServerResponse.status(500).bodyValue("Error retrieving customers");
                });
    }

    @Override
    public Mono<ServerResponse> updateCustomer(ServerRequest request) {
        String phone = request.pathVariable("phone");
        return request.bodyToMono(CustomerRequestDTO.class)
                .flatMap(dto -> handlerValidator.validate(dto)
                        .flatMap(validDto -> customerServicePort.updateCustomer(
                                phone,
                                customerMapper.toDomainFromCreatedRequest(validDto))
                        )
                )
                .doOnSuccess(customerDomain -> log.info("Customer updated successfully: {}", customerDomain.getPhone()))
                .flatMap(customerDomain -> ServerResponse
                        .ok()
                        .bodyValue(customerMapper.toResponseDTOFromDomain(customerDomain)))
                .onErrorResume(e -> {
                    log.error("Error updating customer: {}", e.getMessage());
                    return ServerResponse.badRequest().bodyValue(e.getMessage());
                });
    }

    @Override
    public Mono<ServerResponse> deleteCustomer(ServerRequest request) {
        String phone = request.pathVariable("phone");
        return customerServicePort.deleteCustomer(phone)
                .doOnSuccess(customerDomain -> log.info("Customer deleted successfully: {}", phone))
                .then(Mono.defer(() -> ServerResponse.ok().build()))
                .onErrorResume(e -> {
                    log.error("Error deleting customer: {}", e.getMessage());
                    return ServerResponse.notFound().build();
                });
    }
}
