package eu.paack.sdk.api.validator;

import eu.paack.sdk.api.model.response.Error;
import eu.paack.sdk.model.Customer;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerValidator implements PaackValidator<Customer> {

    public static final Pattern emailPattern = Pattern.compile("^[-!#-\\'*+\\/-9=?^-~]+(?:\\.[-!#-\\'*+\\/-9=?^-~]+)*@[-!#-\\'*+\\/-9=?^-~]+(?:\\.[-!#-\\'*+\\/-9=?^-~]+)+$", Pattern.CASE_INSENSITIVE);
    public static final Pattern phonePattern = Pattern.compile("^(?:(?:\\(?(?:00|\\+)([1-4]\\d\\d|[1-9]\\d?)\\)?)?[\\-\\.\\ \\\\\\/]?)?((?:\\(?\\d{1,}\\)?[\\-\\.\\ \\\\\\/]?){0,})(?:[\\-\\.\\ \\\\\\/]?(?:#|ext\\.?|extension|x)[\\-\\.\\ \\\\\\/]?(\\d+))?$", Pattern.CASE_INSENSITIVE);

    @Override
    public Optional<Error> checkForErrors(Customer request) {
        if (request == null) {
            return createError("001", "Label", "Request must not be null");
        }

        if (isBlank(request.getFirstName())) {
            return createError("001", "FirstName", "FirstName cannot be empty");
        }

        int firstNameLength = request.getFirstName().length();

        if (firstNameLength > 128) {
            createError("002", "FirstName", "First name must have a maximum of 128 characters");
        }

        if (!isBlank(request.getLastName()) && request.getLastName().length() > 128) {
            createError("002", "LastName", "Last name must have a maximum of 45 characters");
        }

        if (!isBlank(request.getEmail()) && request.getEmail().length() < 6 && request.getEmail().length() > 128) {
            createError("002", "Email", "Email must be equal or greater then 6 and equal or less then 128 characters");
        }

        if (!isBlank(request.getPhone()) && request.getPhone().length() > 128) {
            createError("002", "Phone", "Phone must be equal or less then 128 characters");
        }

        if (!isBlank(request.getPhone()) && checkRegexPattern(emailPattern, request.getEmail())) {
            createError("003", "Email", "Email does not correspond to the regex pattern or does not belong to the expected list");
        }

        if (!isBlank(request.getPhone()) && checkRegexPattern(phonePattern, request.getPhone())) {
            createError("003", "Phone", "Phone does not correspond to the regex pattern or does not belong to the expected list");
        }

        return Optional.empty();
    }

    private boolean checkRegexPattern(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }
}
