package eu.paack.sdk.model;

import lombok.*;

import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeOrder {
    String directExternalID;
    String reverseExternalID;
    Customer customer;
    Address deliveryAddress;
    TimeSlot expectedDeliveryTs;
    String serviceType;
    List<Parcel> directParcels;
    List<Parcel> reverseParcels;
    List<ExtraDetail> orderDetails;
    Address pickUpAddress;
    TimeSlot expectedPickUpTs;
    Money insured;
    Money cashOnDelivery;
    Address undeliverableAddress;
    String undeliverableInstructions;
    List<String> clusters;
}