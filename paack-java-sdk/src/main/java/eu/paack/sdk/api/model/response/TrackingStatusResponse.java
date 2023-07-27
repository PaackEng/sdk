package eu.paack.sdk.api.model.response;

import eu.paack.sdk.model.Tracking;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class TrackingStatusResponse {

    private List<Tracking> data;
    private Links links;

}
