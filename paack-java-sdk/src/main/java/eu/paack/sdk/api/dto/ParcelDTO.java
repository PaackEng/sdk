package eu.paack.sdk.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParcelDTO {
    private String barcode;
    private Double height;
    private Double length;
    private Double weight;
    private Double width;
    @JsonProperty("volumetric_weight")
    private Double volumetricWeight;
    @JsonProperty("parcel_details")
    private List<ExtraDetailDTO> parcelDetails;
    private String type;
}
