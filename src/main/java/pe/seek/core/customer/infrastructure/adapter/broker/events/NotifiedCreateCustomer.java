package pe.seek.core.customer.infrastructure.adapter.broker.events;

import lombok.Builder;

@Builder
public record NotifiedCreateCustomer(
        String fullName,
        String phone
) {
}
