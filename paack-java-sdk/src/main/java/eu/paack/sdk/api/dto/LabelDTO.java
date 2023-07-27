package eu.paack.sdk.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class LabelDTO extends OrderDTO{

    @JsonProperty("parcel_number")
    Integer parcelNumber;
    @JsonProperty("template_id")
    private Integer templateId;

    public static LabelDTOBuilder<?, ?> toBuilder(OrderDTO p) {
        return new LabelDTOBuilderImpl().$fillValuesFromParent(p);
    }
}
