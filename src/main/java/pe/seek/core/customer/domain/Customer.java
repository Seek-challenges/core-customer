package pe.seek.core.customer.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Customer {
    private String firstName;
    private String lastName;
    private String phone;
    private String age;
    private LocalDate birthDate;
}
