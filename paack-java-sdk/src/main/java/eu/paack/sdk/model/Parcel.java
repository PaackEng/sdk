package eu.paack.sdk.model;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Parcel {

    private String barcode;
    private Double height;
    private Double length;
    private Double weight;
    private Double width;
    private Double volumetricWeight;
    private String type;
    private List<ExtraDetail> parcelDetails;
    private WeightUnit weightUnit;
    private LengthUnits lengthUnit;
}
