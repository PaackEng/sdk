package eu.paack.sdk.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder(toBuilder=true)
public class Label extends Order {

    private Integer parcelNumber;
    private Integer templateId;

    public static Label.LabelBuilder<?, ?> toBuilder(Order p) {
        return new Label.LabelBuilderImpl().$fillValuesFromParent(p);
    }
}
