package eu.paack.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.paack.sdk.PaackConstants;
import eu.paack.sdk.api.model.response.CheckCoverageResponse;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.exceptions.ApiException;
import eu.paack.sdk.model.PaackCoverage;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Verifies whether the postcode or postcode zone specified in the query parameter is covered by Paack.
 * Returns all the covered postcodes and postcode zones if no query parameter is specified.
 *
 * More details can be found in the Paack API documentation: https://paack.readme.io/reference/coverage
 */
@Slf4j
@SuperBuilder
@NoArgsConstructor
public class CoverageApi extends PaackApi {

    /**
     * Returns all covered postcodes and postcode zones.
     *
     * @return
     */
    public PaackResponse<CheckCoverageResponse, Error> checkCoverage() {
        try {
            PaackResponse<CheckCoverageResponse, Error> response = apiClient.invokeAPI(PaackEndpoint.coverage,
                    "GET",
                    null,
                    null,
                    null,
                    new TypeReference<CheckCoverageResponse>() {
                    });
            log.info(response.toString());
            return PaackResponse.<CheckCoverageResponse, Error>builder()
                    .data(response.getData())
                    .build();
        } catch (ApiException e) {
            log.error("Coverage service failed to load", e);
            return errorMessage("CoverageApi.checkCoverage", "Failed to ge the data:" + e.getMessage());
        }
    }

    /**
     * For a given coverage_zone we need then to check its coverage not only by the complete coverage_zone.
     *
     * Returns True for coverage or False for no coverage.
     *
     * Args:
     * country (str): Country code in ISO 3166 alpha 2 format
     * 	coverage_zone (str):  Coverage zone
     * @param country
     * @param coverage_zone
     * @return
     */
    public PaackResponse<Boolean, Error> checkCoverageZone(String country, String coverage_zone) {
        if (country == null || country.length() == 0) {
            return errorMessage("Country cannot be empty", "CoverageAPI.checkCoverageZone.country", "001");
        }

        if (coverage_zone == null || coverage_zone.length() == 0) {
            return errorMessage("Coverage zone cannot be empty", "CoverageAPI.checkCoverageZone.country", "001");
        }
        try {
            PaackResponse<PaackCoverage, Error> response = apiClient.invokeAPI(PaackEndpoint.coverage,
                    "GET",
                    null,
                    mapQueryParamsZone(country, coverage_zone),
                    null,
                    new TypeReference<PaackCoverage>() {
                    });
            log.info(response.toString());

            if (response.getData() != null) {
                return PaackResponse.<Boolean, Error>builder()
                        .data("coverage".equalsIgnoreCase(response.getData().getMessage()))
                        .build();
            } else
                return errorMessage("CoverageApi.getCoveragePostalCode", "Failed to ge the data:" + response.getError().toString());
        } catch (ApiException e) {
            log.error("Coverage service failed to load", e);
            return errorMessage("CoverageApi.checkCoverageZone", "Failed to ge the data:" + e.getMessage());
        }
    }

    /**
     * For a given postcode we need then to check its coverage not only by the complete postcode.
     *
     * Returns True for coverage or False for no coverage.
     *
     * Args:
     * country (str): Country code in ISO 3166 alpha 2 format
     * coverage_code (str): Postcode of the location
     * @param country
     * @param coverage_code
     * @return
     */
    public PaackResponse<Boolean, Error> getCoveragePostalCode(String country, String coverage_code) {
        if (country == null || country.length() == 0) {
            return errorMessage("Country cannot be empty", "CoverageAPI.getCoveragePostalCode.country", "001");
        }

        if (coverage_code == null || coverage_code.length() == 0) {
            return errorMessage("Coverage code cannot be empty", "CoverageAPI.getCoveragePostalCode.country", "001");
        }
        try {
            PaackResponse<PaackCoverage, Error> response = apiClient.invokeAPI(PaackEndpoint.coverage,
                    "GET",
                    null,
                    mapQueryParamsCode(country, coverage_code),
                    null,
                    new TypeReference<PaackCoverage>() {
                    });
            log.info(response.toString());
            if (response.getData() != null) {
                return PaackResponse.<Boolean, Error>builder()
                        .data("coverage".equalsIgnoreCase(response.getData().getMessage()))
                        .build();
            } else
                return errorMessage("CoverageApi.getCoveragePostalCode", "Failed to ge the data:" + response.getError().toString());

        } catch (ApiException e) {
            log.error("Coverage service failed to load", e);
            return errorMessage("CoverageApi.getCoveragePostalCode", "Failed to ge the data:" + e.getMessage());
        }
    }

    private List<NameValuePair> mapQueryParamsCode(String country, String coverage_code) {
        return Stream.of(
                param(PaackConstants.PARAM_COUNTRY, country),
                param(PaackConstants.PARAM_COVERAGE_CODE, coverage_code)
        ).collect(Collectors.toList());
    }

    private List<NameValuePair> mapQueryParamsZone(String country, String coverage_zone) {
        return Stream.of(
                param(PaackConstants.PARAM_COUNTRY, country),
                param(PaackConstants.PARAM_COVERAGE_ZONE, coverage_zone)
        ).collect(Collectors.toList());
    }

}
