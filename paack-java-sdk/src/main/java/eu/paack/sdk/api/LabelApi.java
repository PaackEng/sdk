package eu.paack.sdk.api;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.paack.sdk.api.converter.LabelConverter;
import eu.paack.sdk.api.dto.LabelDTO;
import eu.paack.sdk.api.dto.ParcelDTO;
import eu.paack.sdk.model.Label;
import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.*;
import eu.paack.sdk.api.validator.LabelValidator;
import eu.paack.sdk.exceptions.ApiException;
import eu.paack.sdk.model.LabelFormat;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Create and return labels for an order.
 *
 * More details can be found in the Paack API documentation: https://paack.readme.io/reference/labels
 */
@Slf4j
@SuperBuilder
@NoArgsConstructor
public class LabelApi extends PaackApi {

    @Builder.Default
    private LabelValidator validator = new LabelValidator();

    /**
     * Create and return a label for an order.
     * Defines the format of the label. 1 is for ZPL; null or missing property returns a PDF
     * Args:
     * payload (LabelCreateRequest)
     * labelFormat (LabelFormat)
     * @param request
     * @param labelFormat
     * @return
     */
    public PaackResponse<LabelCreateResponse, Error> labelCreate(Label request, LabelFormat labelFormat) {
        if (request == null) {
            return errorMessage("Request must not be null", "GenerateLabelRequest", "001");
        }

        if (labelFormat == LabelFormat.SINGLE_ZPL_FILE) {
            request.setTemplateId(1);
        } else if (labelFormat == LabelFormat.MULTI_ZPL_FILE) {
            request.setTemplateId(2);
        } else
            request.setTemplateId(null);

        Optional<Error> error = validator.checkForErrors(request);
        if (error.isPresent()) {
            return errorMessage(error.get());
        }

        LabelDTO labelDTO = LabelConverter.toDTO(request);
        try {
            PaackResponse<byte[], Error> response = apiClient.invokeAPI(PaackEndpoint.label,
                    "POST",
                    null,
                    null,
                    labelDTO,
                    new TypeReference<byte[]>() {
                    });
            log.info(response.toString());

            if (response.getError() != null && !response.getError().isEmpty()) {
                return errorMessage(response.getError());
            }

            if (response.getStatusCode() == 206) {
                if (isValid(new String(response.getData(), StandardCharsets.UTF_8))) {
                    return errorMessage("LabelApi.labelCreate", "Failed to generate label");
                }
            }

            LabelCreateResponse labelCreateResponse;
            if (labelFormat == LabelFormat.SINGLE_ZPL_FILE || labelFormat == LabelFormat.MULTI_ZPL_FILE) {
                labelCreateResponse = new LabelCreateResponseZlp();
                labelCreateResponse.setZplFile(true);
                ((LabelCreateResponseZlp) labelCreateResponse).setLabel(new String(response.getData(), StandardCharsets.UTF_8));
            } else {
                labelCreateResponse = new LabelCreateResponsePdf();
                labelCreateResponse.setZplFile(false);
                ((LabelCreateResponsePdf) labelCreateResponse).setLabel(response.getData());
            }
            return PaackResponse.<LabelCreateResponse, Error>builder()
                    .data(labelCreateResponse)
                    .statusCode(response.getStatusCode())
                    .error(response.getError())
                    .build();
        } catch (ApiException e) {
            log.error("Failed to generate label", e);
            return errorMessage("LabelApi.labelCreate", e.getMessage());
        }
    }

    /**
     * Create and return a label FOR EACH parcel in the order.
     * Defines the format of the label. 1 is for ZPL; null or missing property returns a PDF
     * Args:
     * payload (LabelCreateRequest)
     * labelFormat (LabelFormat)
     * @param request
     * @param labelFormat
     * @return
     */
    public List<PaackResponse<LabelCreateResponse, Error>> labelCreateByParcel(Label request, LabelFormat labelFormat) {
        List<PaackResponse<LabelCreateResponse, Error>> paackResponses = new ArrayList<>();
        List<ParcelDTO> parcels;

        LabelDTO labelDTO = LabelConverter.toDTO(request);
        if (labelDTO.getParcels().size() > 0) {
            parcels = labelDTO.getParcels();

            for (ParcelDTO parcel : parcels) {
                labelDTO.setParcels(new ArrayList<>(Collections.singletonList(parcel)));
                paackResponses.add(labelCreate(LabelConverter.toModel(labelDTO), labelFormat));
            }
        }
        return paackResponses;
    }

    private boolean isValid(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readTree(json);
        } catch (JacksonException e) {
            return false;
        }
        return true;
    }
}
