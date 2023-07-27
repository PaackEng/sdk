package eu.paack.sdk.api.validator;

import eu.paack.sdk.api.model.response.Error;

import java.util.Optional;

public interface PaackValidator<T> {

    Optional<Error> checkForErrors(T model);

    default Optional<Error> createError(String code, String source, String details) {
        return Optional.of(Error.builder()
                .code(code)
                .source(source)
                .detail(details)
                .status("400")
                .message("Local validation of the provided request data failed.")
                .build());
    }

    default boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((!Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }
}
