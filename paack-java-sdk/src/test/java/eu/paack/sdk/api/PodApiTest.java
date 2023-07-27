package eu.paack.sdk.api;

import eu.paack.sdk.Paack;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.api.model.response.ProofOfDeliveryResponse;
import eu.paack.sdk.config.Domain;
import org.junit.Before;
import org.junit.Test;

public class PodApiTest {
    Paack paack;

    private String externalId = "N1215221671111669957";

    @Before
    public void setUp() {
        String clientId = System.getEnv("CLIENT_ID");
        String clientSecret = System.getEnv("CLIENT_SECRET");

        paack = new Paack(clientId, clientSecret, Domain.STAGING);
    }

    @Test
    public void deliveryVerification() {

        PaackResponse<ProofOfDeliveryResponse, Error> response = paack.proofOfDelivery().deliveryVerification(externalId);
        assert response.getError() == null;

    }
}