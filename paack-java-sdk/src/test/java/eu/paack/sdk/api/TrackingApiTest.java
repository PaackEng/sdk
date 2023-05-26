package eu.paack.sdk.api;

import eu.paack.sdk.Paack;
import eu.paack.sdk.api.model.request.StatusesRequest;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.config.Domain;
import eu.paack.sdk.model.Tracking;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Slf4j
public class TrackingApiTest {
    private static Paack paack;

    @BeforeClass
    public static void setUp() {
        String clientId = System.getEnv("CLIENT_ID");
        String clientSecret = System.getEnv("CLIENT_SECRET");

        paack = new Paack(clientId, clientSecret, Domain.STAGING);
    }

    @Test
    public void test_status_with_none_existing_external_id() {

        PaackResponse<List<Tracking>, Error> resp =
                paack.tracking().getStatus("200000V3004");

        assertNotNull(resp);
        assertNull(resp.getError());
    }

    @Test
    public void test_statuses() {
        StatusesRequest statusesRequest =
                StatusesRequest
                        .builder()
                        .start(LocalDateTime.parse("2022-12-12T10:04:05", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .end(LocalDateTime.parse("2023-01-01T10:04:05", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .count(Integer.valueOf(30))
                        .build();
        PaackResponse<List<Tracking>, Error> resp =
                paack.tracking().listStatuses(statusesRequest);

        assertNotNull(resp);
        assertNull(resp.getError());
    }

    @Test
    public void test_statuses_with_none_existing_externalIds() {
        StatusesRequest statusesRequest =
                StatusesRequest
                        .builder()
                        .externalIds(Collections.singletonList("200000V3004"))
                        .start(LocalDateTime.parse("2022-12-12T10:04:05", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .end(LocalDateTime.parse("2023-01-01T10:04:05", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .count(Integer.valueOf(30))
                        .build();
        PaackResponse<List<Tracking>, Error> resp =
                paack.tracking().listStatuses(statusesRequest);

        assertNotNull(resp);
        assertNull(resp.getError());
    }
}