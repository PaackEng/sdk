package eu.paack.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.paack.sdk.PaackConstants;
import eu.paack.sdk.api.model.request.RetailerGetRequest;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.api.model.response.RetailerGetResponse;
import eu.paack.sdk.api.model.response.RetailerLocationResponse;
import eu.paack.sdk.api.validator.RetailerLocationValidator;
import eu.paack.sdk.exceptions.ApiException;
import eu.paack.sdk.model.RetailerLocation;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static eu.paack.sdk.api.PaackEndpoint.retailer;

/**
 * Retailer Location Api is a public service available for retailers to manage their locations (store / warehouse addresses).
 * You can use it to create, retrieve, update and delete locations.
 *
 * More details can be found in the Paack API documentation: https://paack.readme.io/reference/orders-tracking-requests
 */
@Slf4j
@SuperBuilder
@NoArgsConstructor
public class RetailerLocationApi extends PaackApi {

    @Builder.Default
    private RetailerLocationValidator retailerRequestValidator = new RetailerLocationValidator();

    /**
     * Creates a new retailer location with a name, alias, type (store / warehouse) and address.
     * Args:
     * payload: The retailer location data to create
     * Returns:
     * RetailerLocationResponse | Error
     * @param request
     * @return
     */
    public PaackResponse<RetailerLocationResponse, Error> create(RetailerLocation request) {

        Optional<Error> error = retailerRequestValidator.checkForErrors(request);
        if(error.isPresent()) {
            return errorMessage(error.get());
        }

        try {
            PaackResponse<RetailerLocationResponse, Error> response = apiClient.invokeAPI(retailer,
                    "POST",
                    null,
                    null,
                    request,
                    new TypeReference<RetailerLocationResponse>() {
                    });
            log.info(response.toString());
            return PaackResponse.<RetailerLocationResponse, Error>builder()
                    .data(response.getData())
                    .error(response.getError())
                    .build();
        } catch (ApiException e) {
            log.error("Failed to create the retailer location.", e);
            return errorMessage("RetailerLocationApi.create", e.getMessage());
        }
    }

    /**
     * Remove a retailer location.
     *
     * Args:
     * retailerLocationId: The retailer location ID to update
     * Returns:
     * DeleteOrderResponse | ErrorResponse
     * @param retailerLocationId
     * @return
     */
    public PaackResponse<RetailerLocation, Error> delete(String retailerLocationId) {
        if (retailerLocationId == null || retailerLocationId.length() == 0) {
            return errorMessage("RetailerId cannot be empty", "retailerId", "001");
        }
        try {
            PaackResponse<RetailerLocation, Error> response = apiClient.invokeAPI(retailer,
                    "DELETE",
                    Collections.singletonList(param(PaackConstants.PARAM_RETAILER_LOCATION_ID, retailerLocationId)),
                    null,
                    null,
                    new TypeReference<RetailerLocation>() {
                    });
            log.info(response.toString());
            return PaackResponse.<RetailerLocation, Error>builder()
                    .data(response.getData())
                    .build();
        } catch (ApiException e) {
            log.error("Failed to delete the retailer location.", e);
            return errorMessage("RetailerLocationApi.delete", e.getMessage());
        }
    }

    /**
     * Update an existing retailer location.
     *
     * Args:
     * retailerLocationId: The retailer location ID to update
     * payload: The retailer location data to update
     * Returns:
     * RetailerLocationResponse | Error
     * @param request
     * @return
     */
    public PaackResponse<RetailerLocationResponse, Error> patch(RetailerLocation request) {
        if (request.getId()== null || request.getId().length() == 0) {
            return errorMessage("Retailer location id cannot be empty", "Id", "001");
        }
        try {
            PaackResponse<RetailerLocationResponse, Error> response = apiClient.invokeAPI(retailer,
                    "PATCH",
                    Collections.singletonList(param(PaackConstants.PARAM_RETAILER_LOCATION_ID, request.getId())),
                    null,//Collections.singletonList(params),
                    request,
                    new TypeReference<RetailerLocationResponse>() {
                    });
            log.info(response.toString());
            return PaackResponse.<RetailerLocationResponse, Error>builder()
                    .data(response.getData())
                    .error(response.getError())
                    .build();
        } catch (ApiException e) {
            log.error("Failed to update the retailer location.", e);
            return errorMessage("RetailerLocationApi.patch", e.getMessage());
        }
    }

    /**
     * Retrieve a list of retailer locations.
     *
     * Args:
     * GetRetailerLocationRequest
     * Returns:
     * GetRetailerLocationResponse | ErrorResponse
     * @param request
     * @return
     */
    public PaackResponse<List<RetailerLocation>, Error> get(RetailerGetRequest request){
        try {
            PaackResponse<RetailerGetResponse, Error> response = apiClient.invokeAPI(retailer,
                    "GET",
                    null,
                    mapQueryParams(request),
                    null,
                    new TypeReference<RetailerGetResponse>() {
                    });
            log.info(response.toString());
            return PaackResponse.<List<RetailerLocation>, Error>builder()
                    .data(response.getData() == null ? null : response.getData().getLocations())
                    .error(response.getError())
                    .build();
        } catch (ApiException e) {
            log.error("Failed to retrieve the retailer locations.", e);
            return errorMessage("RetailerLocationApi.get", e.getMessage());
        }
    }

    private List<NameValuePair> mapQueryParams(RetailerGetRequest request) {
        List<NameValuePair> pairList = new ArrayList<>();
        if (request.getRetailerId() != null) {
            pairList.add(param("retailer_id", request.getRetailerId()));
        }

        if (request.getId() != null) {
            pairList.add(param("id", request.getId()));
        }

        if (request.getRetailerName() != null) {
            pairList.add(param("retailer_name", request.getRetailerName()));
        }

        if (request.getLocationName() != null) {
            pairList.add(param("location_name", request.getLocationName()));
        }

        if (request.getCity() != null) {
            pairList.add(param("city", request.getCity()));
        }

        if (request.getPostcode() != null) {
            pairList.add(param("postcode", request.getPostcode()));
        }

        if (request.getAlias() != null) {
            pairList.add(param("alias", request.getAlias()));
        }

        if (request.getType() != null) {
            pairList.add(param("type", request.getType()));
        }
        return pairList;
    }


}
