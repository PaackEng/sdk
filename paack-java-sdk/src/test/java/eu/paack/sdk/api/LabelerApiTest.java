package eu.paack.sdk.api;

import eu.paack.sdk.Paack;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.LabelCreateResponse;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.config.Domain;
import eu.paack.sdk.model.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertNull;

public class LabelerApiTest {

    private static Paack paack;

    @BeforeClass
    public static void setUp() {
        String clientId = "47SoYRqAZWdd26Cify0fwLlxtsO50F4R";
        String clientSecret = "MxVunSMrU-8UDOqpUeR7aTzVF7lB4XVz-8jjEjsO4tN_4xZlg54_iTGlnQYN9Plp";

        paack = new Paack(clientId, clientSecret, Domain.STAGING);
    }

    @Test
    public void test_labelCreateByParcel() {

        Label label = createLabelMultiParcels();
        List<PaackResponse<LabelCreateResponse, Error>> response = paack.labeler().labelCreateByParcel(label, LabelFormat.SINGLE_ZPL_FILE);

        if (!response.isEmpty()) {
            for (PaackResponse<LabelCreateResponse, Error> resp: response) {
                assertNull(resp.getError());
            }
        }
    }

    @Test
    public void test_labelCreateSingleZplLabel() {

        Label label = createLabelSingleParcel();
        PaackResponse<LabelCreateResponse, Error> response = paack.labeler().labelCreate(label, LabelFormat.SINGLE_ZPL_FILE);


        assertNull(response.getError());

    }

    @Test
    public void test_labelCreateMultiZplLabel() {

        Label label = createLabelSingleParcel();
        PaackResponse<LabelCreateResponse, Error> response = paack.labeler().labelCreate(label, LabelFormat.MULTI_ZPL_FILE);

        assertNull(response.getError());
    }
    private Label createLabelMultiParcels() {
        Customer customer = Customer.builder()
                .firstName("Martín")
                .lastName("Fierro")
                .build();
        Address deliveryAddress = Address.builder()
                .city("Barcelona")
                .country(Country.SPAIN)
                .line1("Via Augusta")
                .line2("17, Principal")
                .postCode("08006")
                .build();
        Address pickupAddress = Address.builder()
                .city("Barcelona")
                .country(Country.SPAIN)
                .line1("Via Augusta")
                .line2("17, Principal")
                .postCode("08006")
                .build();
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        LocalDateTime endTime = LocalDateTime.now().plusHours(8);
        TimeSlot timeSlot = TimeSlot.builder()
                .start(startTime)
                .end(endTime)
                .build();
        Parcel parcel1 = Parcel.builder()
                .barcode("034724878233029420")
                .height(28.2d)
                .width(48.3d)
                .length(38.1d)
                .weight(12.1d)
                .volumetricWeight(14.2d)
                .type("standard")
                .weightUnit(WeightUnit.KILOGRAM)
                .lengthUnit(LengthUnits.CENTIMETER)
                .build();

        Parcel parcel2 = Parcel.builder()
                .barcode("034724878233029499")
                .height(19.0d)
                .length(12.33d)
                .weight(18.3d)
                .width(33.1d)
                .weightUnit(WeightUnit.KILOGRAM)
                .lengthUnit(LengthUnits.CENTIMETER)
                .type("standard")
                .build();
        Parcel parcel3 = Parcel.builder()
                .barcode("034724878233029437")
                .height(18.5d)
                .length(9.3d)
                .weight(2.5d)
                .width(13.33d)
                .weightUnit(WeightUnit.KILOGRAM)
                .lengthUnit(LengthUnits.CENTIMETER)
                .type("standard")
                .build();


        Label label = Label.builder()
                .templateId(1)
                .customer(customer)
                .deliveryAddress(deliveryAddress)
                .pickupAddress(pickupAddress)
                .expectedDeliveryTs(timeSlot)
                .parcelNumber(2)
                .externalId("5cef0f5-1a0-44-b0f-96faa0")
                .serviceType("NT4")
                .build();

        label.setParcels(Stream.of(parcel1, parcel2, parcel3).collect(Collectors.toList()));

        return label;
    }
    private Label createLabelSingleParcel() {
        Customer customer = Customer.builder()
                .firstName("Martín")
                .lastName("Fierro")
                .build();
        Address deliveryAddress = Address.builder()
                .city("Barcelona")
                .country(Country.SPAIN)
                .line1("Via Augusta")
                .line2("17, Principal")
                .postCode("08006")
                .build();
        Address pickupAddress = Address.builder()
                .city("Barcelona")
                .country(Country.SPAIN)
                .line1("Via Augusta")
                .line2("17, Principal")
                .postCode("08006")
                .build();
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        LocalDateTime endTime = LocalDateTime.now().plusHours(8);
        TimeSlot timeSlot = TimeSlot.builder()
                .start(startTime)
                .end(endTime)
                .build();
        Parcel parcel1 = Parcel.builder()
                .barcode("034724878233029420")
                .height(28.2d)
                .width(48.3d)
                .length(38.1d)
                .weight(12.1d)
                .volumetricWeight(14.2d)
                .type("standard")
                .weightUnit(WeightUnit.KILOGRAM)
                .lengthUnit(LengthUnits.CENTIMETER)
                .build();


        Label label = Label.builder()
                .templateId(1)
                .customer(customer)
                .deliveryAddress(deliveryAddress)
                .pickupAddress(pickupAddress)
                .expectedDeliveryTs(timeSlot)
                .parcelNumber(2)
                .externalId("5cef0f5-1a0-44-b0f-96faa0")
                .serviceType("NT4")
                .build();

        label.setParcels(Stream.of(parcel1).collect(Collectors.toList()));

        return label;
    }

}