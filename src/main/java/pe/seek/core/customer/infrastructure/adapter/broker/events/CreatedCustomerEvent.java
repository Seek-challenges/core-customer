package pe.seek.core.customer.infrastructure.adapter.broker.events;

public record CreatedCustomerEvent(
        String firstName,
        String lastName,
        String phone,
        String age,
        String birthDate
) {
}
