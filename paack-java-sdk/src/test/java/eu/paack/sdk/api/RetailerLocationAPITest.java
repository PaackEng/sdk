package eu.paack.sdk.api;

import eu.paack.sdk.Paack;
import eu.paack.sdk.api.model.request.RetailerGetRequest;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.api.model.response.RetailerLocationResponse;
import eu.paack.sdk.config.Domain;
import eu.paack.sdk.model.Country;
import eu.paack.sdk.model.RetailerLocationAddress;
import eu.paack.sdk.model.RetailerLocation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNull;

public class RetailerLocationAPITest {

    Paack paack;

    @Before
    public void setUp() {
        String clientId = System.getEnv("CLIENT_ID");
        String clientSecret = System.getEnv("CLIENT_SECRET");

        paack = new Paack(clientId, clientSecret, Domain.STAGING);
    }

    @Test
    public void testCreate() {
        RetailerLocationAddress address = RetailerLocationAddress.builder()
                .city("Barcelona")
                .country(Country.SPAIN.getCountryCode())
                .county("Comtat de Barcelona")
                .line1("Via Augusta 88")
                .line2("17, Principal")
                .post_code("08006")
                .build();

        RetailerLocation retailerRequest = RetailerLocation.builder()
                .address(address)
                .alias("TestingRL281240")
                .retailerId("866b263f-ff80-47e8-83d8-56345f151e6f")
                .locationName("Testing Retailer281241")
                .retailerName("Testing Retailer31")
                .type("Store")
                .build();

        PaackResponse<RetailerLocationResponse, Error> response = paack.retailerLocation().create(retailerRequest);

        assertNull(response.getError());

        paack.retailerLocation().delete(response.getData().getRetailerLocationId());
    }

    @Test
    public void testDelete() {
        RetailerLocationAddress address = RetailerLocationAddress.builder()
                .city("Barcelona")
                .country(Country.SPAIN.getCountryCode())
                .county("Comtat de Barcelona")
                .line1("Via Augusta 88")
                .line2("17, Principal")
                .post_code("08006")
                .build();

        RetailerLocation retailerRequest = RetailerLocation.builder()
                .address(address)
                .alias("TestingRL281240")
                .retailerId("866b263f-ff80-47e8-83d8-56345f151e6f")
                .locationName("Testing Retailer281241")
                .retailerName("Testing Retailer31")
                .type("Store")
                .build();

        PaackResponse<RetailerLocation, Error> response = paack.retailerLocation()
                .delete("ab4ce553-ad39-4cfa-ba84-0f4b9b22efcf");

        assertNull(response.getError());
    }

    @Test
    public void testGet() {
        RetailerGetRequest retailerGetRequest = RetailerGetRequest.builder().alias("TestingRL281235").build();

        PaackResponse<List<RetailerLocation>, Error> response = paack.retailerLocation()
                .get(retailerGetRequest);

        assertNull(response.getError());
    }
}
