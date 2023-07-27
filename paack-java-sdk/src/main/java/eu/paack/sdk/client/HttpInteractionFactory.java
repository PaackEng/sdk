package eu.paack.sdk.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;

import java.util.Collection;
import java.util.Collections;

public class HttpInteractionFactory {

    public HttpClient createHttpClient(int maxRetries) {
        return createHttpClient(maxRetries, null);
    }

    public HttpClient createHttpClient(int maxRetries, DefaultRoutePlanner routePlanner) {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                .setRedirectStrategy(getDefaultRedirectStrategy())
                .setDefaultHeaders(getDefaultHeaders())
                .disableCookieManagement()
                .setRetryHandler(getDefaultRetryHandler(maxRetries))
                .setServiceUnavailableRetryStrategy(getDefaultServiceUnavailableRetryStrategy(maxRetries));

        if (routePlanner != null) {
            clientBuilder.setRoutePlanner(routePlanner);
        }

        return clientBuilder.build();
    }

    public Collection<? extends Header> getDefaultHeaders() {
        return Collections.singletonList(new BasicHeader("X-PAACK-SDK-VERSION", "Java/1.0.0"));
    }

    public RedirectStrategy getDefaultRedirectStrategy() {
        return new LaxRedirectStrategy();
    }

    public HttpRequestRetryHandler getDefaultRetryHandler(int maxRetries) {
        return new DefaultHttpRequestRetryHandler(maxRetries, true);
    }

    public ServiceUnavailableRetryStrategy getDefaultServiceUnavailableRetryStrategy(int maxRetries) {
        return new ServiceUnavailableRetryStrategy() {
            private int waitPeriod = 100;

            @Override
            public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
                waitPeriod *= 2;
                return executionCount <= maxRetries &&
                        response.getStatusLine().getStatusCode() >= 500; //important!
            }

            @Override
            public long getRetryInterval() {
                return waitPeriod;
            }
        };
    }

    public RequestConfig createRequestConfig(int connectionRequestTimeout, int connectTimeout, int socketTimeout) {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout * 1000)
                .setConnectTimeout(connectTimeout * 1000)
                .setSocketTimeout(socketTimeout * 1000)
                .build();
    }

    public ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

}
