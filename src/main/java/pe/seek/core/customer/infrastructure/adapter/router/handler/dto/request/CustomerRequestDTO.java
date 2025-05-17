package pe.seek.core.customer.infrastructure.adapter.router.handler.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import pe.seek.core.customer.infrastructure.adapter.router.handler.dto.request.group.CreatedCustomerGroup;

public record CustomerRequestDTO(
        @NotBlank(groups = CreatedCustomerGroup.class)
        String firstName,
        @NotBlank(groups = CreatedCustomerGroup.class)
        String lastName,
        @NotBlank(groups = CreatedCustomerGroup.class)
        String phone,
        @Min(value = 0, groups = CreatedCustomerGroup.class)
        Integer age,
        @NotBlank(groups = CreatedCustomerGroup.class)
        String birthDate
) {
}
