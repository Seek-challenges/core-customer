package pe.seek.core.customer.infrastructure.adapter.router.handler.dto.response;

import java.time.LocalDate;

public record CustomerResponseDTO(
        String firstName,
        String lastName,
        String phone,
        String age,
        LocalDate birthDate
) {
}
