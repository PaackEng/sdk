package eu.paack.sdk.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.paack.sdk.client.HttpInteractionFactory;
import eu.paack.sdk.config.PaackConfig;
import eu.paack.sdk.exceptions.AuthenticationException;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AuthenticationManager {

    private final HttpInteractionFactory httpFactory;
    private final Map<String, PaackToken> paackTokens;
    private PaackConfig paackConfig;
    private ObjectMapper mapper;
    private HttpClient httpClient;
    private RequestConfig requestConfig;


    public AuthenticationManager(HttpInteractionFactory httpFactory, PaackConfig config) {
        this.httpFactory = httpFactory;
        this.paackTokens = Collections.synchronizedMap(new PassiveExpiringMap<>(getExpirationPolicy()));
        setPaackConfig(config);
    }

    public PaackConfig getPaackConfig() {
        return paackConfig;
    }

    public void setPaackConfig(PaackConfig config) {
        paackConfig = config;
        httpClient = httpFactory.createHttpClient(config.getMaxRetries());
        requestConfig = httpFactory.createRequestConfig(config.getConnectionRequestTimeout(), config.getConnectTimeout(), config.getSocketTimeout());
        mapper = httpFactory.createObjectMapper();
    }

    private PassiveExpiringMap.ExpirationPolicy<String, PaackToken> getExpirationPolicy() {
        return (key, value) -> {
            if (value == null || value.getExpiresIn() < 10L) {
                return -1L;
            }

            final long now = System.currentTimeMillis();
            final long expiresIn = (value.getExpiresIn() - 300L) * 1000L; // convert to millis
            if (now > Long.MAX_VALUE - expiresIn) {
                // expiration would be greater than Long.MAX_VALUE
                // never expire
                return -1L;
            }
            return now + expiresIn;
        };
    }

    public PaackToken createAccessToken(String audience) {
        if (audience == null) {
            audience = getPaackConfig().getOAuth().getAudience();
        }

        PaackToken token;
        if (paackTokens != null) {
            token = paackTokens.get(audience);
            if (token == null) {
                token = requestNewToken(audience);
            }
        } else {
            token = requestNewToken(audience);
        }
        return token;
    }

    private PaackToken requestNewToken(String audience) {
        try {
            final HttpPost request = new HttpPost(paackConfig.getOAuth().getIssuerUrl());
            request.setConfig(requestConfig);
            request.setEntity(new UrlEncodedFormEntity(buildRequestParams(audience)));
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < 200 || statusCode >= 400) {
                throw new AuthenticationException("Got an error response from the Authentication API: " + response);
            }
            HttpEntity entity = response.getEntity();
            PaackToken token = mapper.readValue(EntityUtils.toString(entity), PaackToken.class);
            paackTokens.put(audience, token);
            return token;
        } catch (IOException e) {
            throw new AuthenticationException("Couldn't create authentication token.", e);
        }
    }

    private List<NameValuePair> buildRequestParams(String audience) {

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", paackConfig.getOAuth().getGrantType()));
        params.add(new BasicNameValuePair("audience", audience));
        params.add(new BasicNameValuePair("client_id", paackConfig.getOAuth().getClientId()));
        params.add(new BasicNameValuePair("client_secret", paackConfig.getOAuth().getClientSecret()));
        return params;
    }
}
