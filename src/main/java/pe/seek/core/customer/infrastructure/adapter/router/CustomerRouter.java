package pe.seek.core.customer.infrastructure.adapter.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import pe.seek.core.customer.infrastructure.adapter.router.handler.CustomerHandlerPort;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
class CustomerRouter {

    private static final String BASE_PATH = "/v1/customers";

    @Bean
    public RouterFunction<ServerResponse> customerRoutes(CustomerHandlerPort customerHandler) {
        return route(POST(BASE_PATH), customerHandler::createCustomer)
                .andRoute(POST(BASE_PATH + "/{phone}"), customerHandler::getCustomerByPhone)
                .andRoute(POST(BASE_PATH + "/all"), customerHandler::getAllCustomers)
                .andRoute(POST(BASE_PATH + "/update/{phone}"), customerHandler::updateCustomer)
                .andRoute(POST(BASE_PATH + "/delete/{phone}"), customerHandler::deleteCustomer);
    }
}
