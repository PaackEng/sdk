package eu.paack.sdk.api.converter;

import eu.paack.sdk.api.dto.ExtraDetailDTO;
import eu.paack.sdk.model.ExtraDetail;

import java.util.List;
import java.util.stream.Collectors;

public class ExtraDetailConverter {

    public static List<ExtraDetailDTO> toDTO(List<ExtraDetail> extraDetails) {
        return extraDetails == null ? null : extraDetails.stream()
                .map(ExtraDetailConverter::toDTO)
                .collect(Collectors.toList());
    }

    public static List<ExtraDetail> toModel(List<ExtraDetailDTO> extraDetails) {
        return extraDetails == null ? null : extraDetails.stream()
                .map(ExtraDetailConverter::toModel)
                .collect(Collectors.toList());
    }

    public static ExtraDetailDTO toDTO(ExtraDetail extraDetail) {
        if (extraDetail == null) {
            return null;
        }

        return ExtraDetailDTO.builder()
                .name(extraDetail.getName())
                .type(extraDetail.getType())
                .value(extraDetail.getValue())
                .build();
    }

    public static ExtraDetail toModel(ExtraDetailDTO extraDetailDTO) {
        if (extraDetailDTO == null) {
            return null;
        }

        return ExtraDetail.builder()
                .name(extraDetailDTO.getName())
                .type(extraDetailDTO.getType())
                .value(extraDetailDTO.getValue())
                .build();
    }
}
