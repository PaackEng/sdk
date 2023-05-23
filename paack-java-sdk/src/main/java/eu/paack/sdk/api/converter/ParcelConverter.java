package eu.paack.sdk.api.converter;

import eu.paack.sdk.api.dto.ParcelDTO;
import eu.paack.sdk.model.LengthUnits;
import eu.paack.sdk.model.Parcel;
import eu.paack.sdk.model.WeightUnit;
import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;
import java.text.ParseException;

@Slf4j
public class ParcelConverter {
    public static ParcelDTO toDTO(Parcel parcel) {

        if (parcel == null) {
            return null;
        }

        return ParcelDTO.builder()
                .barcode(parcel.getBarcode())
                .height(parcel.getHeight() == null ? 0d : parcel.getHeight())
                .length(parcel.getLength() == null ? 0d : parcel.getLength())
                .weight(parcel.getWeight() == null ? 0d : parcel.getWeight())
                .width(parcel.getWidth() == null ? 0d : parcel.getWidth())
                .volumetricWeight(parcel.getVolumetricWeight() == null ? 0d : parcel.getVolumetricWeight())
                .parcelDetails(ExtraDetailConverter.toDTO(parcel.getParcelDetails()))
                .type(parcel.getType())
                .build();
    }

    public static Parcel toModel(ParcelDTO parcelDTO) {

        if (parcelDTO == null) {
            return null;
        }

        return Parcel.builder()
                .barcode(parcelDTO.getBarcode())
                .height(parcelDTO.getHeight())
                .length(parcelDTO.getLength())
                .weight(parcelDTO.getWeight())
                .width(parcelDTO.getWidth())
                .volumetricWeight(parcelDTO.getVolumetricWeight())
                .weightUnit(WeightUnit.KILOGRAM)
                .lengthUnit(LengthUnits.CENTIMETER)
                .type(parcelDTO.getType())
                .parcelDetails(ExtraDetailConverter.toModel(parcelDTO.getParcelDetails()))
                .build();
    }
}
