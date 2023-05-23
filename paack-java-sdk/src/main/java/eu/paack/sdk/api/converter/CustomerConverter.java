package eu.paack.sdk.api.converter;

import eu.paack.sdk.api.dto.CustomerDTO;
import eu.paack.sdk.model.Customer;
import eu.paack.sdk.model.CustomerType;
import eu.paack.sdk.model.Language;

public class CustomerConverter {

    public static CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        return CustomerDTO.builder()
                .address(AddressConverter.toDTO(customer.getAddress()))
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .hasGdprConsent(customer.getHasGdprConsent())
                .type(customer.getCustomerType() == null ? null : customer.getCustomerType().name().toLowerCase())
                .customerDetails(ExtraDetailConverter.toDTO(customer.getCustomerDetails()))
                .language(customer.getLanguage() == null ? null : customer.getLanguage().getLanguageCode())
                .build();
    }

    public static Customer toModel(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }

        return Customer.builder()
                .address(AddressConverter.toModel(customerDTO.getAddress(), null))
                .firstName(customerDTO.getFirstName())
                .lastName(customerDTO.getLastName())
                .email(customerDTO.getEmail())
                .phone(customerDTO.getPhone())
                .hasGdprConsent(customerDTO.getHasGdprConsent())
                .customerType(CustomerType.fromString(customerDTO.getType()))
                .customerDetails(ExtraDetailConverter.toModel(customerDTO.getCustomerDetails()))
                .language(Language.fromString(customerDTO.getLanguage()))
                .build();
    }
}