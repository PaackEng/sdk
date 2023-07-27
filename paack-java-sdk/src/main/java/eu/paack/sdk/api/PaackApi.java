package eu.paack.sdk.api;

import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.client.ApiClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.util.Collections;
import java.util.List;


@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaackApi {

    @Getter
    @Setter
    protected ApiClient apiClient;

    protected BasicNameValuePair param(String name, String value) {
        return new BasicNameValuePair(name, value);
    }

    protected <T> PaackResponse<T, Error> errorMessage(String source, String details) {
        return errorMessage(Error.builder()
                .code("000")
                .source(source)
                .detail(details)
                .status("400")
                .message("There was a problem calling the API resulting in an ApiException.")
                .build());
    }

    protected <T> PaackResponse<T, Error> errorMessage(String details, String source, String code) {
        return errorMessage(Error.builder()
                .code(code)
                .source(source)
                .detail(details)
                .status("400")
                .message("Local validation of the provided request data failed.")
                .build());
    }

    protected <T> PaackResponse<T, Error> errorMessage(Error error) {
        return PaackResponse.<T, Error>builder()
                .error(Collections.singletonList(error))
                .build();
    }

    protected <T> PaackResponse<T, Error> errorMessage(List<Error> errors) {
        return PaackResponse.<T, Error>builder()
                .error(errors)
                .build();
    }

}
