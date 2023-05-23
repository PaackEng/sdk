package eu.paack.sdk;

import eu.paack.sdk.model.Label;
import eu.paack.sdk.api.model.request.GetOrderRequest;
import eu.paack.sdk.api.validator.LabelValidator;
import eu.paack.sdk.api.model.response.*;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.config.Domain;
import eu.paack.sdk.model.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {
        String clientId = "47SoYRqAZWdd26Cify0fwLlxtsO50F4R";
        String clientSecret = "MxVunSMrU-8UDOqpUeR7aTzVF7lB4XVz-8jjEjsO4tN_4xZlg54_iTGlnQYN9Plp";

        Paack paackClient = new Paack(clientId, clientSecret, Domain.STAGING);

        try {
            //                List<Tracking> value =
            //                        paackClient.track().getStatus(Arrays.asList("200000V3004"));
            //                System.out.println(value.get(0).getExternalId());
            //                StatusesRequest statusesRequest =
            //                        StatusesRequest.builder().externalIds(Arrays.asList("200000V3004")).count((short) 10)
            //                                .build();
            //                List<TrackingHistoryItem> statuses =
            //                        paackClient.track().listStatuses(statusesRequest);

            //Get order request
            GetOrderRequest getOrderRequest = GetOrderRequest.builder()
                    .externalId("200000V3004")
                    .include("label")
                    .labelFormat("PDF").build();
            paackClient.order().getById(getOrderRequest);

            //Coverage call example
            PaackResponse<CheckCoverageResponse, Error> resp =
                    paackClient.coverage().checkCoverage();


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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse("2022-11-17 10:00:00", formatter);
            LocalDateTime endTime = LocalDateTime.parse("2022-11-17 17:00:00", formatter);
            TimeSlot timeSlot = TimeSlot.builder()
                    .start(startTime)
                    .end(endTime)
                    .build();
            Order order = Order.builder()
                    .externalId("lordelete-b288-404d-8bbd-e0d32eac2990")
                    .customer(customer)
                    .deliveryAddress(address)
                    .expectedDeliveryTs(timeSlot)
                    .serviceType("ST2")
                    .pickupAddress(address)
                    .build();
//                paackClient.order().create(request);


            Address uaddress = Address.builder()
                    .city("Barcelona")
                    .line1("Carrer del Taquigraf Garriga, 1")
                    .postCode("08030")
                    .country(Country.SPAIN)
                    .build();


            Customer lCustomer = Customer.builder()
                    .firstName("Martin")
                    .lastName("Fierro")
                    .build();
            Address pD = Address.builder()
                    .city("Barcelona")
                    .country(Country.SPAIN)
                    .line1("Via Augusta")
                    .line2("17, Principal")
                    .postCode("08006")
                    .build();
            TimeSlot pTs = TimeSlot.builder()
                    .start(startTime)
                    .build();

            Address pP = Address.builder()
                    .city("Barcelona")
                    .country(Country.SPAIN)
                    .line1("Avinguda Diagonal, 1234")
                    .postCode("08021")
                    .build();


            Parcel parcel = Parcel.builder()
                    .barcode("034724878233029420")
                    .height(28.2d)
                    .width(48.3d)
                    .length(38.1d)
                    .weight(12.1d)
                    .build();
            Parcel parcel2 = Parcel.builder()
                    .barcode("034724878233029421")
                    .height(28.2d)
                    .width(48.3d)
                    .length(38.1d)
                    .weight(12.1d)
                    .build();

            ExtraDetail extraDetail = ExtraDetail.builder()
                    .name("sales_number")
                    .type("string")
                    .value("g_328298234984981")
                    .build();
            Label pdfLabelRequest = Label.builder()
                    .externalId("5cef0f5-1a0-44-b0f-96faa0")
                    .customer(lCustomer)
                    .deliveryAddress(pD)
                    .orderDetails(Collections.singletonList(extraDetail))
                    .expectedDeliveryTs(pTs)
                    .pickupAddress(pP)
                    .serviceType("NT4")
                    .parcels(Arrays.asList(parcel, parcel2))
                    .build();
            PaackResponse<LabelCreateResponse, Error> label = paackClient.labeler().labelCreate(pdfLabelRequest, null);
            if (label.getData() != null) {
                Path path = Paths.get("/Users/andrei.draghia/Downloads/paack-java-sdk3/" + pdfLabelRequest.getExternalId() + ".pdf");
                Files.write(path, ((LabelCreateResponsePdf) label.getData()).getLabel());
            }

            Label zplLabelRequest = Label.builder()
                    .externalId("5cef0f5-1a0-44-b0f-96faa0")
                    .templateId(1)
                    .customer(lCustomer)
                    .deliveryAddress(pD)
                    .orderDetails(Collections.singletonList(extraDetail))
                    .expectedDeliveryTs(pTs)
                    .pickupAddress(pP)
                    .serviceType("NT4")
                    .parcels(Collections.singletonList(parcel))
                    .build();

//                resp = paackClient.labeler().generate(zplLabelRequest);
//                path = Paths.get("/Users/istvan.takacs/Downloads/paack/" + resp.getFileName() + ".zpl");
//                Files.write(path, resp.getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
