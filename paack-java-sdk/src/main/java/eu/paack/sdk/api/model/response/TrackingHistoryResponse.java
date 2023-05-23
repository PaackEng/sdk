package eu.paack.sdk.api.model.response;

import lombok.Data;

import java.util.List;

@Data
public class TrackingHistoryResponse {

    List<TrackingHistoryItem> data;
    private String[] errors;
    private Links links;
}
