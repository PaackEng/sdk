package eu.paack.sdk.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
@Data
@ToString
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @JsonProperty("external_id")
    private String externalId;
    @JsonProperty("service_type")
    private String serviceType;
    private CustomerDTO customer;
    @JsonProperty("delivery_address")
    private AddressDTO deliveryAddress;
    @JsonProperty("expected_delivery_ts")
    private TimeSlotDTO expectedDeliveryTs;
    private List<ParcelDTO> parcels;
    @JsonProperty("pick_up_address")
    private AddressDTO pickupAddress;
    @JsonProperty("order_details")
    private List<ExtraDetailDTO> orderDetails;
    @JsonProperty("expected_pickup_ts")
    private TimeSlotDTO expectedPickUpTs;
    @JsonProperty("undeliverable_address")
    private AddressDTO undeliverableAddress;
    @JsonProperty("undeliverable_instructions")
    private String undeliverableInstructions;
    @JsonProperty("delivery_instructions")
    private String deliveryInstructions;
    @JsonProperty("pick_up_instructions")
    private String pickUpInstructions;
    @JsonProperty("deliver_type")
    private String deliverType;
    private String status;
    @JsonProperty("cod_currency")
    private String codCurrency;
    @JsonProperty("cod_value")
    private Double codValue;
    @JsonProperty("delivery_model")
    private String deliveryModel;
    private String labels;
    private List<String> clusters;
    private Double insuredValue;
    private String insuredCurrency;

    public abstract static class OrderDTOBuilder<C extends OrderDTO, B extends OrderDTO.OrderDTOBuilder<C, B>> {
        protected B $fillValuesFromParent(OrderDTO instance) {
            $fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }
    }
}
