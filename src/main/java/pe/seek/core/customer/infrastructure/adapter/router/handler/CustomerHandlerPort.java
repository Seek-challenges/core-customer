package pe.seek.core.customer.infrastructure.adapter.router.handler;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

public interface CustomerHandlerPort {
    Mono<ServerResponse> createCustomer(ServerRequest request);
    Mono<ServerResponse> getCustomerByPhone(ServerRequest request);
    Mono<ServerResponse> getAllCustomers(ServerRequest request);
    Mono<ServerResponse> updateCustomer(ServerRequest request);
    Mono<ServerResponse> deleteCustomer(ServerRequest request);
}
