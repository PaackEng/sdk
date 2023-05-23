package eu.paack.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.paack.sdk.PaackConstants;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.api.model.response.ProofOfDeliveryResponse;
import eu.paack.sdk.exceptions.ApiException;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.message.BasicNameValuePair;

import java.util.Collections;

/**
 * Proof of Delivery API allows you to easily validate that your orders have been delivered.
 * It works by extracting additional delivery verification data that is sent from the Paack driver application to Paack's database.
 *
 * More details can be found in the Paack API documentation: https://paack.readme.io/reference/orders-proof-of-delivery
 */
@Slf4j
@SuperBuilder()
@NoArgsConstructor
public class PodApi extends PaackApi {

    /**
     * Retrieves the delivery verifications for the retailer of the order external ID specified in the path parameter.
     * @param externalId
     * @return
     */
    public PaackResponse<ProofOfDeliveryResponse, Error> deliveryVerification(String externalId) {
        if (externalId == null || externalId.length() == 0) {
            return errorMessage("ExternalId cannot be empty", "externalId", "001");
        }
        try {
            BasicNameValuePair params = new BasicNameValuePair(PaackConstants.PARAM_EXTERNAL_ID, externalId);
            PaackResponse<ProofOfDeliveryResponse, Error> response = apiClient.invokeAPI(PaackEndpoint.pod,
                    "GET",
                    Collections.singletonList(params),
                    null,
                    null,
                    new TypeReference<ProofOfDeliveryResponse>() {
                    });
            log.info(response.toString());
            return response;
        } catch (ApiException e) {
            log.error("Delivery verification failed", e);
            return errorMessage("PodApi.deliveryVerification", "Delivery verification failed");
        }
    }
}
