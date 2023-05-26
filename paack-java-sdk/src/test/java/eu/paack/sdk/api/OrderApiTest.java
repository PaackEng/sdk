package eu.paack.sdk.api;

import eu.paack.sdk.Paack;
import eu.paack.sdk.api.model.request.*;
import eu.paack.sdk.api.model.response.*;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.config.Domain;
import eu.paack.sdk.model.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.OrderWith;
import org.junit.runner.manipulation.Alphanumeric;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertNull;

@OrderWith(Alphanumeric.class)
public class OrderApiTest {
    private static Paack paack;

    private static String externalId;

    @BeforeClass
    public static void setUp() {
        String clientId = System.getEnv("CLIENT_ID");
        String clientSecret = System.getEnv("CLIENT_SECRET");
        externalId = generateExternalId();

        paack = new Paack(clientId, clientSecret, Domain.STAGING);
    }

    private static String generateExternalId() {
        String id = "test_order_api_";
        Random random = new Random();
        int num = random.nextInt(100000);
        String formatted = String.format("%07d", num);
        return id + formatted;
    }

    @Test
    public void testA_create() {

        Address address = Address.builder()
                .city("Barcelona")
                .line1("Carrer del Taquigraf Garriga, 125")
                .postCode("08029")
                .country(Country.SPAIN)
                .build();

        Customer customer = Customer.builder()
                .address(address)
                .firstName("Pendro")
                .lastName("Rodri")
                .email("pedro@email.com")
                .hasGdprConsent(true)
                .build();

        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        LocalDateTime endTime = LocalDateTime.now().plusHours(6);
        TimeSlot timeSlot = TimeSlot.builder()
                .start(startTime)
                .end(endTime)
                .build();
        Order order = Order.builder()
                .externalId(externalId)
                .customer(customer)
                .deliveryAddress(address)
                .expectedDeliveryTs(timeSlot)
                .serviceType("ST2")
                .pickupAddress(address)
                .build();

        Parcel parcel = Parcel.builder()
                .barcode("012345678901234000")
                .height(28.1d)
                .width(48.2d)
                .length(38.3d)
                .weight(12.4d)
                .build();
        order.setParcels(Stream.of(parcel).collect(Collectors.toList()));
        PaackResponse<OrderCreateSuccessResponse, Error> resp =
                paack.order().createWithWarehouse(order, false);

        assertNull(resp.getError());
    }

    @Test
    public void testB_get() {

        GetOrderRequest getReq = GetOrderRequest.builder()
                .externalId(externalId)
                .include("include")
                .labelFormat("PDF").build();
        PaackResponse<OrderResponse, Error> response = paack.order().getById(getReq);

        assertNull (response.getError());
    }

    @Test
    public void testD_updateAddress() {
        Address uaddress = Address.builder()
                .city("Barcelona")
                .line1("Carrer del Taquigraf Garriga, 1")
                .postCode("08006")
                .country(Country.SPAIN)
                .build();

        PaackResponse<UpdateOrderResponse, Error> response = paack.order().updateDeliveryAddress(externalId, uaddress);
        assertNull(response.getError());
    }

    @Test
    public void testF_updateParcels() {
        Parcel parcel = Parcel.builder()
                .barcode("012345678901234000")
                .height(28.1d)
                .width(48.2d)
                .length(38.3d)
                .weight(12.4d)
                .build();
        Parcel parcel2 = Parcel.builder()
                .barcode("012345678901234001")
                .height(28.5d)
                .width(48.6d)
                .length(38.7d)
                .weight(12.8d)
                .build();
        Parcel parcel3 = Parcel.builder()
                .barcode("012345678901234002")
                .height(28.3d)
                .width(48.5d)
                .length(38.7d)
                .weight(12.9d)
                .build();

        PaackResponse<UpdateOrderResponse, Error> response = paack.order().updateParcels(externalId, Stream.of(parcel, parcel2, parcel3).collect(Collectors.toList()));
        assertNull(response.getError());
    }

    @Test
    public void testG_updateParcel() {
        Parcel parcel = Parcel.builder()
                .barcode("012345678901234000")
                .height(28.3d)
                .width(48.5d)
                .length(38.7d)
                .weight(12.9d)
                .build();
        PaackResponse<UpdateOrderResponse, Error> response = paack.order().updateParcel(externalId, parcel);
        assertNull(response.getError());
    }

    @Test
    public void testZ_delete() {

        PaackResponse<Order, Error> deleteResp = paack.order().delete(externalId);
        assertNull(deleteResp.getError());
    }

    @Test
    public void test_updateCustomerContactDetails() {

        String email = "test@paack.com";
        String phone = "6664443331";
        boolean hasGDPRConsent = true;

        PaackResponse<UpdateOrderResponse, Error> response = paack.order().updateCustomerContactDetails(externalId, email, phone, hasGDPRConsent);

        if (response.getError() != null) {
            System.out.println(response.getError().toString());
        }
        assertNull(response.getError());
    }

    @Test
    public void test_upsertOrderWithStore() {
        Order upsertOrder = createUpsertOrder();
        upsertOrder.setExternalId(generateExternalId());
        PaackResponse<OrderCreateSuccessResponse, Error> response = paack.order().upsertOrderWithStore(upsertOrder);

        if (response.getError() != null) {
            System.out.println(response.getError().toString());
        }
        assertNull(response.getError());
    }

    @Test
    public void testCancel() {
        Address address = Address.builder()
                .city("Barcelona")
                .line1("Carrer del Taquigraf Garriga, 125")
                .postCode("08029")
                .country(Country.SPAIN)
                .build();

        Customer customer = Customer.builder()
                .address(address)
                .firstName("Pendro")
                .lastName("Rodri")
                .email("pedro@email.com")
                .hasGdprConsent(true)
                .build();

        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        LocalDateTime endTime = LocalDateTime.now().plusHours(6);
        TimeSlot timeSlot = TimeSlot.builder()
                .start(startTime)
                .end(endTime)
                .build();
        Order order = Order.builder()
                .externalId(externalId)
                .customer(customer)
                .deliveryAddress(address)
                .expectedDeliveryTs(timeSlot)
                .serviceType("ST2")
                .pickupAddress(address)
                .build();

        Parcel parcel = Parcel.builder()
                .barcode("012345678901234000")
                .height(28.1d)
                .width(48.2d)
                .length(38.3d)
                .weight(12.4d)
                .build();
        order.setParcels(Stream.of(parcel).collect(Collectors.toList()));
        paack.order().createWithWarehouse(order, false);

        PaackResponse<Boolean, Error> paackResponse =  paack.order().cancelRequest(externalId);
        assertNull(paackResponse.getError());
    }

    @Test
    public void test_upsertOrderWithWarehouse() {
        Order upsertOrder = createUpsertOrder();
        upsertOrder.setExternalId(generateExternalId());

        PaackResponse<OrderCreateSuccessResponse, Error> response = paack.order().upsertOrderWithWarehouse(upsertOrder);

        if (response.getError() != null) {
            System.out.println(response.getError().toString());
        }
        assertNull(response.getError());
    }

    @Test
    public void test_exchangeOrderWithWarehouse() {
        ExchangeOrder exchangeOrder = createExchangeOrder();
        exchangeOrder.setDirectExternalID("warehouse_" + generateExternalId());
        exchangeOrder.setReverseExternalID("warehouse_" + generateExternalId());

        PaackResponse<OrderExchangeSuccessResponse, Error> response = paack.order().exchangeWithWarehouse(exchangeOrder);

        if (response.getError() != null) {
            System.out.println(response.getError().toString());
        }
        assertNull(response.getError());
    }

    @Test
    public void test_exchangeOrderWithStore() {
        ExchangeOrder exchangeOrder = createExchangeOrder();
        exchangeOrder.setDirectExternalID("store_" + generateExternalId());
        exchangeOrder.setReverseExternalID("store_" + generateExternalId());
        PaackResponse<OrderExchangeSuccessResponse, Error> response = paack.order().exchangeWithStore(exchangeOrder);

        if (response.getError() != null) {
            System.out.println(response.getError().toString());
        }
        assertNull(response.getError());
    }

    private ExchangeOrder createExchangeOrder() {

        Country country = Country.SPAIN;
        Address customerAddress = Address.builder()
                .city("Barcelona1")
                .country(country)
                .county("Comtat de Barcelona1")
                .line2("17, Principal1")
                .line1("Via Augusta1")
                .postCode("08006")
                .build();

        Customer customer = Customer.builder()
                .firstName("Martín1")
                .lastName("Fierro1")
                .email("martin.fierro@paack.com1")
                .phone("+343973418041")
                .hasGdprConsent(true)
                .language(Language.ENGLISH)
                .address(customerAddress)
                .build();

        Address deliveryAddress = Address.builder()
                .city("Barcelona1")
                .country(country)
                .line2("18, Principal1")
                .line1("Via Augusta1")
                .county("Comtat de Barcelona1")
                .postCode("08006")
                .instructions("Leave at the door1")
                .build();

        Address undeliverableAddress = Address.builder()
                .city("Barcelona")
                .country(country)
                .county("Comtat de Barcelona1")
                .line2("12341")
                .line1("Avinguda Diagonal1")
                .postCode("08021")
                .instructions("Open parcel1")
                .build();

        Address pickupAddress = Address.builder()
                .city("Barcelona")
                .country(country)
                .county("Comtat de Barcelona1")
                .line2("12341")
                .line1("Avinguda Diagonal1")
                .postCode("08021")
                .instructions("Check opening hours1")
                .build();

        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        LocalDateTime endTime = LocalDateTime.now().plusHours(6);
        TimeSlot timeSlot = TimeSlot.builder()
                .start(startTime)
                .end(endTime)
                .build();

        Money money = new Money();
        money.setCurrency("EUR");
        money.setAmount("20.1");
        Money cod = new Money();
        cod.setCurrency("EUR");
        cod.setAmount("25.1");
        ExchangeOrder exchangeOrder = ExchangeOrder.builder()
                .customer(customer)
                .deliveryAddress(deliveryAddress)
                .serviceType("ST2")
                .undeliverableAddress(undeliverableAddress)
                .pickUpAddress(pickupAddress)
                .insured(money)
                .cashOnDelivery(cod)
                .expectedDeliveryTs(timeSlot)
                .expectedPickUpTs(timeSlot)
                .build();

        Parcel parcel1 = Parcel.builder()
                .barcode("034724878233029421")
                .height(28.2d)
                .width(48.2d)
                .length(38.2d)
                .weight(12.2d)
                .volumetricWeight(14.2d)
                .type("standard")
                .weightUnit(WeightUnit.KILOGRAM)
                .lengthUnit(LengthUnits.CENTIMETER)
                .build();

        Parcel parcel2 = Parcel.builder()
                .barcode("DRINK6TEST-1-2")
                .height(2.2d)
                .length(2.2d)
                .volumetricWeight(13.2d)
                .weight(2.2d)
                .width(2.2d)
                .weightUnit(WeightUnit.KILOGRAM)
                .lengthUnit(LengthUnits.CENTIMETER)
                .type("standard")
                .build();

        exchangeOrder.setDirectParcels(Stream.of(parcel1, parcel2).collect(Collectors.toList()));
        return exchangeOrder;
    }

    private Order createUpsertOrder() {

        Country country = Country.SPAIN;
        Address customerAddress = Address.builder()
                .city("Barcelona1")
                .country(country)
                .line2("17, Principal1")
                .line1("Via Augusta1")
                .postCode("08006")
                .build();
        Customer customer = Customer.builder()
                .firstName("Martín1")
                .lastName("Fierro1")
                .email("martin.fierro@paack.com1")
                .phone("+343973418041")
                .hasGdprConsent(true)
                .address(customerAddress)
                .build();

        Address deliveryAddress = Address.builder()
                .city("Barcelona1")
                .country(country)
                .line2("17, Principal1")
                .line1("Via Augusta1")
                .postCode("08006")
                .instructions("Leave at the door1")
                .build();

        Address undeliverableAddress = Address.builder()
                .city("Barcelona")
                .country(country)
                .county("Comtat de Barcelona1")
                .line2("12341")
                .line1("Avinguda Diagonal1")
                .postCode("08021")
                .instructions("Open parcel1")
                .build();

        Address pickupAddress = Address.builder()
                .city("Barcelona")
                .country(country)
                .county("Comtat de Barcelona1")
                .line2("12341")
                .line1("Avinguda Diagonal1")
                .postCode("08021")
                .instructions("Check opening hours1")
                .build();

        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        LocalDateTime endTime = LocalDateTime.now().plusHours(6);
        TimeSlot timeSlot = TimeSlot.builder()
                .start(startTime)
                .end(endTime)
                .build();

        Money money = new Money();
        money.setCurrency("EUR");
        money.setAmount("20.1");
        Money cod = new Money();
        cod.setCurrency("EUR");
        cod.setAmount("25.1");
        Order upsertOrder = Order.builder()
                .customer(customer)
                .deliveryAddress(deliveryAddress)
                .serviceType("ST2")
                .undeliverableAddress(undeliverableAddress)
                .pickupAddress(pickupAddress)
                .insured(money)
                .cashOnDelivery(cod)
                .expectedDeliveryTs(timeSlot)
                .expectedPickUpTs(timeSlot)
                .build();

        Parcel parcel1 = Parcel.builder()
                .barcode("034724878233029421")
                .height(28.2d)
                .width(48.2d)
                .length(38.2d)
                .weight(12.2d)
                .volumetricWeight(14.2d)
                .type("standard")
                .build();
        Parcel parcel2 = Parcel.builder()
                .barcode("DRINK6TEST-1-2")
                .height(2.2)
                .length(2.2)
                .volumetricWeight(13.2)
                .type("standard")
                .build();
        upsertOrder.setParcels(Stream.of(parcel1, parcel2).collect(Collectors.toList()));
        return upsertOrder;
    }
}

