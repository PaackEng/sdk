package eu.paack.sdk.api.converter;

import eu.paack.sdk.api.dto.OrderDTO;
import eu.paack.sdk.api.dto.ParcelDTO;
import eu.paack.sdk.model.DeliveryType;
import eu.paack.sdk.model.Money;
import eu.paack.sdk.model.Order;
import eu.paack.sdk.model.Parcel;

import java.util.ArrayList;
import java.util.List;

public class OrderConverter {

    public static OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }
        normaliseOrderData(order);

        List<ParcelDTO> parcelDTOList = new ArrayList<>();
        if (order.getParcels() != null) {
            for (Parcel parcel : order.getParcels()) {
                parcelDTOList.add(ParcelConverter.toDTO(parcel));
            }
        }

        String deliveryInstructions = null;
        if (order.getDeliveryAddress() != null) {
            deliveryInstructions = order.getDeliveryAddress().getInstructions();
        }

        String pickupInstructions = null;
        if (order.getPickupAddress() != null) {
            pickupInstructions = order.getPickupAddress().getInstructions();
        }
        String currency = null;
        String codValue = null;
        if (order.getCashOnDelivery() != null) {
            currency = order.getCashOnDelivery().getCurrency();
            codValue = order.getCashOnDelivery().getAmount();
        }
        String insuredCurrency = null;
        String insuredAmount = null;
        if (order.getInsured() != null) {
            insuredAmount = order.getInsured().getAmount();
            insuredCurrency = order.getInsured().getCurrency();
        }
        String undeliverableInstructions = null;
        if (order.getUndeliverableAddress() != null) {
            undeliverableInstructions = order.getUndeliverableAddress().getInstructions();
        }

        return OrderDTO.builder()
                .externalId(order.getExternalId())
                .serviceType(order.getServiceType())
                .customer(CustomerConverter.toDTO(order.getCustomer()))
                .deliveryAddress(AddressConverter.toDTO(order.getDeliveryAddress()))
                .expectedDeliveryTs(TimeSlotConverter.toDTO(order.getExpectedDeliveryTs()))
                .parcels(parcelDTOList)
                .pickupAddress(AddressConverter.toDTO(order.getPickupAddress()))
                .orderDetails(ExtraDetailConverter.toDTO(order.getOrderDetails()))
                .expectedPickUpTs(TimeSlotConverter.toDTO(order.getExpectedPickUpTs()))
                .deliveryInstructions(deliveryInstructions)
                .pickUpInstructions(pickupInstructions)
                .deliverType(order.getDeliveryType()== null ? null : order.getDeliveryType().getDeliveryType())
                .codCurrency(currency)
                .codValue(codValue == null ? null : Double.valueOf(codValue))
                .deliveryModel(order.getDeliveryModel())
                .clusters(order.getClusters())
                .insuredValue(insuredAmount == null ? null : Double.valueOf(insuredAmount))
                .insuredCurrency(insuredCurrency)
                .undeliverableAddress(AddressConverter.toDTO(order.getUndeliverableAddress()))
                .undeliverableInstructions(undeliverableInstructions)
                .build();
    }

    public static Order toModel(OrderDTO orderDTO) {
        if (orderDTO == null) {
            return null;
        }
        List<Parcel> parcels = new ArrayList<>();
        if (orderDTO.getParcels() != null) {
            for (ParcelDTO parcelDTO : orderDTO.getParcels()) {
                parcels.add(ParcelConverter.toModel(parcelDTO));
            }
        }

        Money insured = new Money();
        insured.setAmount(orderDTO.getInsuredValue() == null ? null : orderDTO.getInsuredValue().toString());
        insured.setCurrency(orderDTO.getInsuredCurrency());

        Money cashOnDelivery = new Money();
        cashOnDelivery.setAmount(orderDTO.getCodValue() == null ? null : orderDTO.getCodValue().toString());
        cashOnDelivery.setCurrency(orderDTO.getCodCurrency());

        return Order.builder()
                .externalId(orderDTO.getExternalId())
                .serviceType(orderDTO.getServiceType())
                .customer(CustomerConverter.toModel(orderDTO.getCustomer()))
                .deliveryAddress(AddressConverter.toModel(orderDTO.getDeliveryAddress(), orderDTO.getDeliveryInstructions()))
                .expectedDeliveryTs(TimeSlotConverter.toModel(orderDTO.getExpectedDeliveryTs()))
                .parcels(parcels)
                .pickupAddress(AddressConverter.toModel(orderDTO.getPickupAddress(), orderDTO.getPickUpInstructions()))
                .orderDetails(ExtraDetailConverter.toModel(orderDTO.getOrderDetails()))
                .undeliverableAddress(AddressConverter.toModel(orderDTO.getUndeliverableAddress(), orderDTO.getUndeliverableInstructions()))
                .expectedPickUpTs(TimeSlotConverter.toModel(orderDTO.getExpectedPickUpTs()))
                .deliveryType(DeliveryType.fromString(orderDTO.getDeliverType()))
                .insured(insured)
                .cashOnDelivery(cashOnDelivery)
                .deliveryModel(orderDTO.getDeliveryModel())
                .clusters(orderDTO.getClusters())
                .build();
    }

    private static void normaliseOrderData(Order order) {
        if (order.getCustomer() != null && order.getCustomer().getAddress() == null && order.getDeliveryAddress() != null) {
            order.getCustomer().setAddress(order.getDeliveryAddress());
        }

    }
}
