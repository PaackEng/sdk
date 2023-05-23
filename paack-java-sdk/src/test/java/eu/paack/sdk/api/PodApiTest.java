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
        String clientId = "47SoYRqAZWdd26Cify0fwLlxtsO50F4R";
        String clientSecret = "MxVunSMrU-8UDOqpUeR7aTzVF7lB4XVz-8jjEjsO4tN_4xZlg54_iTGlnQYN9Plp";

        paack = new Paack(clientId, clientSecret, Domain.STAGING);
    }

    @Test
    public void deliveryVerification() {

        PaackResponse<ProofOfDeliveryResponse, Error> response = paack.proofOfDelivery().deliveryVerification(externalId);
        assert response.getError() == null;

    }
}