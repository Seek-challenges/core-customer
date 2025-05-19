package pe.seek.core.customer.infrastructure.adapter.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import pe.seek.core.customer.infrastructure.adapter.router.handler.CustomerHandlerPort;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
class CustomerRouter {

    private static final String BASE_PATH = "/v1/customers";

    @Bean
    public RouterFunction<ServerResponse> customerRoutes(CustomerHandlerPort customerHandler) {
        return route(POST(BASE_PATH), customerHandler::createCustomer)
                .andRoute(GET(BASE_PATH + "/phone/{phone}"), customerHandler::getCustomerByPhone)
                .andRoute(GET(BASE_PATH + "/all"), customerHandler::getAllCustomers)
                .andRoute(PUT(BASE_PATH + "/phone/{phone}"), customerHandler::updateCustomer)
                .andRoute(DELETE(BASE_PATH + "/{phone}"), customerHandler::deleteCustomer);
    }
}
