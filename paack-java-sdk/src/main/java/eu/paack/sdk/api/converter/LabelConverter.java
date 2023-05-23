package eu.paack.sdk.api.converter;

import eu.paack.sdk.api.dto.LabelDTO;
import eu.paack.sdk.api.dto.OrderDTO;
import eu.paack.sdk.model.Label;
import eu.paack.sdk.model.Order;

public class LabelConverter {
    public static LabelDTO toDTO (Label label) {
        if (label == null) {
            return null;
        }

        OrderDTO orderDTO = OrderConverter.toDTO(label);
       return LabelDTO.toBuilder(orderDTO)
                .parcelNumber(label.getParcelNumber())
                .templateId(label.getTemplateId())
                .build();

    }

    public static Label toModel(LabelDTO labelDTO) {
        if (labelDTO == null) {
            return null;
        }

        Order order = OrderConverter.toModel(labelDTO);
        return Label.toBuilder(order)
                .parcelNumber(labelDTO.getParcelNumber())
                .templateId(labelDTO.getTemplateId())
                .build();
    }
}
