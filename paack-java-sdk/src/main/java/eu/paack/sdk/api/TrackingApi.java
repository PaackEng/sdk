package eu.paack.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.paack.sdk.PaackConstants;
import eu.paack.sdk.api.model.request.StatusesRequest;
import eu.paack.sdk.api.validator.StatusesRequestValidator;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.api.model.response.TrackingHistoryResponse;
import eu.paack.sdk.api.model.response.TrackingStatusResponse;
import eu.paack.sdk.exceptions.ApiException;
import eu.paack.sdk.model.Tracking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Pull API is a public service available for retailers. You can use it to retrieve the status of your orders, including all of their order history details. Your request is queried against Paack's system. The API returns the relevant information based on the request made.
 *
 * More details can be found in the Paack API documentation: https://paack.readme.io/reference/orders-tracking-requests
 */
@Slf4j
@SuperBuilder
@AllArgsConstructor
public class TrackingApi extends PaackApi {

    @Getter
    @Setter
    private StatusesRequestValidator statusesRequestValidator;

    /**
     * Retrieve the last status of the order with the specified ID.
     * Args:
     * orderId (string): External ID of the order
     * Returns:
     * response (OrderStatusGetResponse | Error): either the status of the order or error response
     * @param externalId
     * @return
     */
    public PaackResponse<List<Tracking>, Error> getStatus(String externalId) {
        if (externalId == null) {
            return errorMessage("External Id must not be null", "externalId", "001");
        }

        try {
            BasicNameValuePair params = new BasicNameValuePair(PaackConstants.PARAM_EXTERNAL_ID, String.join(PaackConstants.COMMA_SEPARATOR, externalId));
            PaackResponse<TrackingStatusResponse, Error> response = apiClient.invokeAPI(PaackEndpoint.tracking_status,
                    "GET",
                    null,
                    Collections.singletonList(params),
                    null,
                    new TypeReference<TrackingStatusResponse>() {
                    });
            log.info(response.toString());
            return PaackResponse.<List<Tracking>, Error>builder()
                    .data(response.getData() != null ? response.getData().getData() : null)
                    .build();
        } catch (ApiException e) {
            log.error("Ger order status failed", e);
            return errorMessage("TrackingApi.getStatus", "Error occurred on status request" + e.getMessage());
        }
    }

    /**
     * Retrieves the order status list of the specified external id(s) for a timeframe in ISO UTC date format. It also takes a count value and an after and before value for pagination. If the response has more than one page it will automatically retrieve the following pages and concatenate the responses. If one of the responses have an error it will return the error.
     *
     * Args:
     * orderIds ([]string]): A list of order Ids to fetch
     * start (string): Start date in ISO format
     * end (string): End date in ISO format
     * count (int): Number of events returned in the response. The default and minimum number is 10. The maximum is 100
     * before (string): The previous ID given to a page of events which can be called in the endpoint for pagination purposes
     * after (string): The next ID given to a page of events which can be called in the endpoint for pagination purposes
     * Returns:
     * response (OrderStatusResponse | Error): either the status of the order or error response
     * @param request
     * @return
     */
    public PaackResponse<List<Tracking>, Error> listStatuses(StatusesRequest request) {
        Optional<Error> error = getStatusesRequestValidator().checkForErrors(request);
        if (error.isPresent()) {
            return errorMessage(error.get());
        }

        try {
            PaackResponse<TrackingHistoryResponse, Error> response =
                    apiClient.invokeAPI(PaackEndpoint.tracking_history,
                            "GET",
                            null,
                            mapQueryParams(request),
                            null,
                            new TypeReference<TrackingHistoryResponse>() {
                            });
            log.info(response.toString());
            List<Tracking> items = null;
            if (response.getData() != null) {
                items = response.getData().getData().stream().
                        map(h -> h.getAttributes()).collect(Collectors.toList());
            }

            if (response.getData() != null && response.getData().getLinks() != null && response.getData().getLinks().getNext() != null &&
                    response.getData().getLinks().getNext().getHref() != null) {
                String nextPage = response.getData().getLinks().getNext().getHref();

                String after = null;
                String end = null;
                String start = null;
                String count = null;
                String externalIds = null;

                while (nextPage != null) {
                    String decodedLink = null;
                    try {
                        decodedLink = java.net.URLDecoder.decode(nextPage, StandardCharsets.UTF_8.name());
                    } catch (UnsupportedEncodingException e) {
                        // not going to happen - value came from JDK's own StandardCharsets
                    }
                    String substring = decodedLink.substring(nextPage.indexOf("?") + 1);
                    String[] split = substring.split("&");
                    for (String stringExtract : split) {
                        String[] parameterSplit = stringExtract.split("=");
                        switch (parameterSplit[0]) {
                            case "externalIds":
                                externalIds = parameterSplit[1];
                            case "after":
                                after = parameterSplit[1];
                                break;
                            case "end":
                                end = parameterSplit[1];
                                break;
                            case "start":
                                start = parameterSplit[1];
                                break;
                            case "count":
                                count = parameterSplit[1];
                        }
                    }

                    PaackResponse<TrackingHistoryResponse, Error> nextResponse =
                            apiClient.invokeAPI(PaackEndpoint.tracking_history,
                                    "GET",
                                    null,
                                    mapQueryParams(after, end, start, count, externalIds),
                                    null,
                                    new TypeReference<TrackingHistoryResponse>() {
                                    });
                    log.info(nextResponse.toString());
                    List<Tracking> nextItems = null;
                    if (nextResponse.getData() != null) {
                        nextItems = nextResponse.getData().getData().stream().
                                map(h -> h.getAttributes()).collect(Collectors.toList());
                    }
                    items.addAll(nextItems);

                    if (response.getData() != null && nextResponse.getData().getLinks() != null && nextResponse.getData().getLinks().getNext() != null &&
                            nextResponse.getData().getLinks().getNext().getHref() != null) {
                        nextPage = nextResponse.getData().getLinks().getNext().getHref();
                    } else
                        nextPage = null;
                }
            }

            return PaackResponse.<List<Tracking>, Error>builder()
                    .data(items)
                    .build();
        } catch (ApiException e) {
            log.error("Get order statuses failed", e);
            return errorMessage("TrackingApi.listStatuses", "Error occurred on statuses request" + e.getMessage());
        }
    }

    private List<NameValuePair> mapQueryParams(StatusesRequest request) {
        return Stream.of(
                param(PaackConstants.PARAM_EXTERNAL_IDS,
                        request.getExternalIds() == null ? null : String.join(PaackConstants.COMMA_SEPARATOR, request.getExternalIds())),
                param(PaackConstants.PARAM_PAGE_COUNT, request.getCount() == null ? null : Integer.toString(request.getCount())),
                param(PaackConstants.PARAM_START, convertDateToString(request.getStart())),
                param(PaackConstants.PARAM_END, convertDateToString(request.getEnd())),
                param(PaackConstants.PARAM_BEFORE, request.getBefore()),
                param(PaackConstants.PARAM_AFTER, request.getAfter())
        ).collect(Collectors.toList());
    }

    private List<NameValuePair> mapQueryParams(String after, String end, String start, String count, String externalIds) {
        return Stream.of(
                param(PaackConstants.PARAM_EXTERNAL_IDS,
                        externalIds == null ? null : String.join(PaackConstants.COMMA_SEPARATOR, externalIds)),
                param(PaackConstants.PARAM_PAGE_COUNT, count),
                param(PaackConstants.PARAM_START, start),
                param(PaackConstants.PARAM_END, end),
                param(PaackConstants.PARAM_AFTER, after)
        ).collect(Collectors.toList());
    }

    private String convertDateToString(LocalDateTime date) {
        if (date == null) {
            return null;
        }

        ZonedDateTime UTCDate = date.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC);
        return UTCDate.format(DateTimeFormatter.ISO_INSTANT);
    }
}
