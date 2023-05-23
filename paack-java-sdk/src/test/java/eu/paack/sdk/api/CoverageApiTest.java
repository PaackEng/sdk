package eu.paack.sdk.api;

import eu.paack.sdk.Paack;
import eu.paack.sdk.api.model.response.CheckCoverageResponse;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.config.Domain;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class CoverageApiTest {

    private static Paack paack;

    @BeforeClass
    public static void setUp() {
        String clientId = "47SoYRqAZWdd26Cify0fwLlxtsO50F4R";
        String clientSecret = "MxVunSMrU-8UDOqpUeR7aTzVF7lB4XVz-8jjEjsO4tN_4xZlg54_iTGlnQYN9Plp";

        paack = new Paack(clientId, clientSecret, Domain.STAGING);
    }

    @Test
    public void test_checkCoverage() {
        PaackResponse<CheckCoverageResponse, Error> resp =
                paack.coverage().checkCoverage();

        assertNotNull(resp);
        assertNotNull(resp.getData());
        assertNull(resp.getError());
    }

    @Test
    public void test_checkCoveragePostalCode() {

        PaackResponse<Boolean, Error> response = paack.coverage().getCoveragePostalCode("ES", "06430");

        assertNull(response.getError());
        assertTrue(response.getData());
    }

    @Test
    public void test_checkCoveragePostalCodeFail() {

        PaackResponse<Boolean, Error> response = paack.coverage().getCoveragePostalCode("ab", "06430");

        assertNotNull(response.getError());
    }

    @Test
    public void test_checkCoverageZone() {

        PaackResponse<Boolean, Error> response = paack.coverage().checkCoverageZone("PT", "2680");

        assertNull(response.getError());
    }

    @Test
    public void test_checkCoverageZoneFail() {

        PaackResponse<Boolean, Error> response = paack.coverage().checkCoverageZone("ab", "2680");

        assertNotNull(response.getError());
    }

}