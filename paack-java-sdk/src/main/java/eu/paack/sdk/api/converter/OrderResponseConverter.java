package eu.paack.sdk.api.converter;

import eu.paack.sdk.api.dto.OrderResponseDTO;
import eu.paack.sdk.api.model.response.OrderResponse;

public class OrderResponseConverter extends OrderConverter {

    public static OrderResponse toModel(OrderResponseDTO orderResponseDTO) {

        if (orderResponseDTO == null) {
            return null;
        }

        return OrderResponse.builder()
                .status(orderResponseDTO.getStatus())
                .labels(orderResponseDTO.getLabels())
                .order(OrderConverter.toModel(orderResponseDTO))
                .build();
    }

}
