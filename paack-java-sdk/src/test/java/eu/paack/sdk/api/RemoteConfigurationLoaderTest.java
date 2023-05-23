package eu.paack.sdk.api;

import eu.paack.sdk.Paack;
import eu.paack.sdk.api.model.response.CheckCoverageResponse;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.config.Domain;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RemoteConfigurationLoaderTest {

    Paack paack;

    @Before
    public void setUp() {
        String clientId = "47SoYRqAZWdd26Cify0fwLlxtsO50F4R";
        String clientSecret = "MxVunSMrU-8UDOqpUeR7aTzVF7lB4XVz-8jjEjsO4tN_4xZlg54_iTGlnQYN9Plp";

        paack = new Paack(clientId, clientSecret, Domain.STAGING);
    }

    @Test
    public void load() {

        PaackResponse<CheckCoverageResponse, Error> resp =
                paack.coverage().checkCoverage();

        assertNotNull(resp);
        assertNotNull(resp.getData());
        assertNull(resp.getError());
    }
}