package eu.paack.sdk.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.paack.sdk.api.PaackEndpoint;
import eu.paack.sdk.api.model.response.PaackResponse;
import eu.paack.sdk.auth.AuthenticationManager;
import eu.paack.sdk.auth.PaackToken;
import eu.paack.sdk.config.PaackConfig;
import eu.paack.sdk.exceptions.ApiException;
import eu.paack.sdk.exceptions.AuthenticationException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class ApiClient {

    @Setter
    @Getter
    private PaackConfig config;

    @Setter
    @Getter
    private AuthenticationManager authenticationManager;

    private CloseableHttpClient httpClient;
    private RequestConfig requestConfig;
    private ObjectMapper objectMapper;

    public ApiClient(HttpInteractionFactory httpFactory, PaackConfig paackConfig) {
        config = paackConfig;
        httpClient = (CloseableHttpClient) httpFactory.createHttpClient(config.getMaxRetries());
        requestConfig = httpFactory.createRequestConfig(config.getConnectionRequestTimeout(), config.getConnectTimeout(), config.getSocketTimeout());
        objectMapper = httpFactory.createObjectMapper();
    }

    public <T, E> PaackResponse<T, E> invokeAPI(PaackEndpoint endpoint,
                                                String method,
                                                List<NameValuePair> routeParams,
                                                List<NameValuePair> queryParams,
                                                Object body,
                                                TypeReference<T> returnType) throws ApiException, AuthenticationException {

        RequestBuilder builder = RequestBuilder.create(method);
        builder.setUri(buildUrl(endpoint, routeParams, queryParams));
        builder.setConfig(requestConfig);
        setAuthorizationHeader(endpoint, builder);

        if (body != null) {
            builder.setEntity(serialize(body, ContentType.APPLICATION_JSON));
        }

        try (CloseableHttpResponse response = httpClient.execute(builder.build())) {
            int statusCode = response.getStatusLine().getStatusCode();
//                        if (endpoint == PaackEndpoint.tracking_history) {
//                            String message = EntityUtils.toString(response.getEntity());
//                            log.info(message);
//                        }
            if (isSuccessfulStatus(statusCode)) {

                return PaackResponse.<T, E>builder()
                        .data(processResponse(response, returnType, statusCode))
                        .statusCode(statusCode)
                        .build();
            } else {
                return PaackResponse.<T, E>builder()
                        .error(Collections.singletonList(processErrorResponse(response, new TypeReference<E>() {
                        })))
                        .statusCode(statusCode)
                        .build();
            }

        } catch (Exception e) {
            throw new ApiException("API call failed with some exception please report", e);
        }
    }

    private void setAuthorizationHeader(PaackEndpoint endpoint, RequestBuilder builder) {
        String audience = endpoint.isGroup() ? getConfig().getAudience(endpoint.getGroup()) : getConfig().getAudience(endpoint.getName());
        PaackToken token = getAuthenticationManager().createAccessToken(audience);
        builder.setHeader("Authorization", "Bearer " + token.getAccessToken());
    }

    private String buildUrl(PaackEndpoint endpoint, List<NameValuePair> routeParams, List<NameValuePair> queryParams) {
        StringBuilder url = new StringBuilder();
        if (endpoint == PaackEndpoint.config) {
            url.append(getConfig().getConfigServer());
            return url.toString();
        }
        if (endpoint.isGroup()) {
            url.append(getConfig().getDomain(endpoint.getGroup()))
                    .append(getConfig().getResource(endpoint.getName(), endpoint.getGroup()));
        } else {
            url.append(getConfig().getDomain(endpoint.getName()))
                    .append(getConfig().getResource(endpoint.getName()));
        }
        //clean up url from all the mess
        int idx = url.indexOf("?");
        if (idx > 0) {
            url.replace(idx, url.length(), "");
        }

        if (routeParams != null && !routeParams.isEmpty()) {
            routeParams.forEach(r -> {
                int rIdx = url.indexOf("{" + r.getName() + "}");
                if (rIdx > 0) {
                    url.replace(rIdx, rIdx + r.getName().length() + 2, r.getValue());
                } else {
                    url.append("/").append(r.getValue());
                }
            });
        }

        if (queryParams != null && !queryParams.isEmpty()) {
            // support (constant) query string in `path`, e.g. "/posts?draft=1"
            String prefix = url.indexOf("?") > 0 ? "&" : "?";
            String qParam = queryParams.stream()
                    .filter(p -> p.getValue() != null)
                    .map(p -> p.getName() + "=" + escapeString(String.valueOf(p.getValue())))
                    .collect(Collectors.joining("&"));
            url.append(prefix).append(qParam);
        }

        return url.toString();
    }

    private <T> T processResponse(CloseableHttpResponse response, TypeReference<T> returnType, int statusCode)
            throws ApiException {

        if (statusCode == HttpStatus.SC_NO_CONTENT) {
            return null;
        }
        return this.deserialize(response, returnType);
    }

    private <E> E processErrorResponse(CloseableHttpResponse response, TypeReference<E> returnType) throws ApiException {
        return this.deserialize(response, returnType);
    }

    public HttpEntity serialize(Object body, ContentType contentType) throws ApiException {
        try {
            String bodyString = objectMapper.writeValueAsString(body);
            log.info(bodyString);
            return new StringEntity(bodyString, contentType);
        } catch (JsonProcessingException e) {
            throw new ApiException(e);
        }
    }

    /**
     * Deserialize response content
     */
    public <T> T deserialize(HttpResponse response, TypeReference<T> valueType)
            throws ApiException {

        if (valueType == null) {
            throw new ApiException("Missing valueType check the implementation");
        }
        HttpEntity entity = response.getEntity();
        Type valueRawType = valueType.getType();
        if (valueRawType.equals(byte[].class)) {
            try {
                return (T) EntityUtils.toByteArray(entity);
            } catch (IOException e) {
                throw new ApiException("Failed to de de-serialize the object", e);
            }
        } else if (valueRawType.equals(File.class)) {

            try {
                return (T) downloadFileFromResponse(response);
            } catch (IOException e) {
                throw new ApiException("Failed to de de-serialize the object", e);
            }
        }
        // Assume json if no mime type
        try {
            return objectMapper.readValue(entity.getContent(), valueType);
        } catch (Exception e) {
            throw new ApiException("Failed to de de-serialize the object", e);
        }
    }

    protected boolean isSuccessfulStatus(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    private File downloadFileFromResponse(HttpResponse response) throws IOException {
        Header contentDispositionHeader = response.getFirstHeader("Content-Disposition");
        String contentDisposition = contentDispositionHeader == null ? null : contentDispositionHeader.getValue();
        File file = prepareDownloadFile(contentDisposition);
        Files.copy(response.getEntity().getContent(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return file;
    }

    protected File prepareDownloadFile(String contentDisposition) throws IOException {
        String filename = null;
        if (contentDisposition != null && !"".equals(contentDisposition)) {
            // Get filename from the Content-Disposition header.
            Pattern pattern = Pattern.compile("filename=['\"]?([^'\"\\s]+)['\"]?");
            Matcher matcher = pattern.matcher(contentDisposition);
            if (matcher.find()) {
                filename = matcher.group(1);
            }
        }

        String prefix;
        String suffix = null;
        if (filename == null) {
            prefix = "download-";
            suffix = "";
        } else {
            int pos = filename.lastIndexOf('.');
            if (pos == -1) {
                prefix = filename + "-";
            } else {
                prefix = filename.substring(0, pos) + "-";
                suffix = filename.substring(pos);
            }
            if (prefix.length() < 3) {
                prefix = "download-";
            }
        }
        return Files.createTempFile(prefix, suffix).toFile();
    }

    public String escapeString(String str) {
        try {
            return URLEncoder.encode(str, "utf8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }
}
