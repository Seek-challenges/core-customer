package pe.seek.core.customer.infrastructure.repository;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import pe.seek.core.shared.common.CopyEntity;

import java.time.LocalDate;
import java.util.Optional;

@Data
@With
@Table("customer")
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity implements CopyEntity<CustomerEntity> {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private Integer age;
    private LocalDate birthDate;

    @Override
    public CustomerEntity copyFrom(CustomerEntity item) {
        Optional.ofNullable(item.getFirstName()).ifPresent(this::setFirstName);
        Optional.ofNullable(item.getLastName()).ifPresent(this::setLastName);
        Optional.ofNullable(item.getPhone()).ifPresent(this::setPhone);
        Optional.ofNullable(item.getAge()).ifPresent(this::setAge);
        Optional.ofNullable(item.getBirthDate()).ifPresent(this::setBirthDate);
        return this;
    }
}
