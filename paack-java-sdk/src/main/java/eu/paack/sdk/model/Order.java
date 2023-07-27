package eu.paack.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder=true)
public class Order {
    private String externalId;
    private DeliveryType deliveryType;
    private String serviceType;
    private Customer customer;
    private Address deliveryAddress;
    private Address undeliverableAddress;
    private TimeSlot expectedDeliveryTs;
    private List<Parcel> parcels;
    private Address pickupAddress;
    private List<ExtraDetail> orderDetails;
    private TimeSlot expectedPickUpTs;
    private Money insured;
    private Money cashOnDelivery;
    private String deliveryModel;
    private List<String> clusters;

    public abstract static class OrderBuilder<C extends Order, B extends Order.OrderBuilder<C, B>> {
        protected B $fillValuesFromParent(Order instance) {
            $fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }
    }
}
