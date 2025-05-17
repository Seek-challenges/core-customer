package pe.seek.core.customer.application.port.output;

import pe.seek.core.customer.domain.Customer;

public interface CustomerCreatedPublisherPort {
    void notifyCreatedCustomer(Customer customer);
}
