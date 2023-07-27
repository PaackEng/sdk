package eu.paack.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Tracking extends Order {

    private String id;
    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("event_name")
    private String eventName;
    @JsonProperty("event_description")
    private String eventDescription;
    @JsonProperty("is_open")
    private Boolean isOpen;
    @JsonProperty("delivery_attempt")
    private String deliveryAttempt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("tracking_url")
    private String trackingUrl;
}
