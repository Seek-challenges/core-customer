package pe.seek.core.customer.infrastructure.mapper;

import org.mapstruct.Mapper;
import pe.seek.core.customer.domain.Customer;
import pe.seek.core.customer.infrastructure.adapter.broker.events.CreatedCustomerEvent;
import pe.seek.core.customer.infrastructure.adapter.router.handler.dto.request.CustomerRequestDTO;
import pe.seek.core.customer.infrastructure.adapter.router.handler.dto.response.CustomerResponseDTO;
import pe.seek.core.customer.infrastructure.repository.CustomerEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toDomainFromEventFallBack(CreatedCustomerEvent event);
    CustomerEntity toEntityFromDomain(Customer customer);
    Customer toDomainFromEntity(CustomerEntity customerEntity);
    List<Customer> toDomainFromEntityList(List<CustomerEntity> customerEntities);

    Customer toDomainFromCreatedRequest(CustomerRequestDTO customerEntity);
    CustomerResponseDTO toResponseDTOFromDomain(Customer customer);
}
