package com.example.oktausersvc.client;

import com.example.oktausersvc.config.AppProperties;
import com.example.oktausersvc.exception.UpstreamAuthException;
import com.example.oktausersvc.util.JwtSigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Exchanges a private_key_jwt client assertion for an access token (Client
 * Credentials).
 * Minimal in-memory cache until expires_in.
 */
@Component
public class TokenClient {

    private static final Logger log = LoggerFactory.getLogger(TokenClient.class);

    private final WebClient webClient;
    private final String tokenUrl;
    private final String scope;
    private final JwtSigner jwtSigner;

    private final AtomicReference<String> cachedToken = new AtomicReference<>(null);
    private volatile long expiresAtEpochSec = 0L;

    public TokenClient(WebClient webClient, AppProperties props) {
        this.webClient = webClient;
        var okta = props.getOkta();
        this.tokenUrl = okta.getDomain() + okta.getTokenPath();
        this.scope = okta.getScope();
        this.jwtSigner = new JwtSigner(
                okta.getClientId(),
                okta.getJwt().getAudience(),
                okta.getJwt().getKid(),
                okta.getJwt().getPemPath());
    }

    /** Returns a valid access token, refreshing if needed. */
    public String getAccessToken() {
        long now = Instant.now().getEpochSecond();
        String token = cachedToken.get();
        if (token != null && now < (expiresAtEpochSec - 10)) {
            log.debug("Using cached access token, expiresAt={}", expiresAtEpochSec);
            return token;
        }
        log.info("No cached token or expired, fetching new one");
        return fetchAndCacheToken();
    }

    private synchronized String fetchAndCacheToken() {
        long now = Instant.now().getEpochSecond();
        String existing = cachedToken.get();
        if (existing != null && now < (expiresAtEpochSec - 10)) {
            log.debug("Another thread already refreshed token, using cached");
            return existing;
        }

        String clientAssertion = jwtSigner.buildClientAssertion();
        log.debug("Built client_assertion (len={})", clientAssertion.length());

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("scope", scope);
        form.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        form.add("client_assertion", clientAssertion);

        log.info("Requesting token from {}", tokenUrl);
        @SuppressWarnings("unchecked")
        Map<String, Object> resp = (Map<String, Object>) webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null || resp.get("access_token") == null) {
            log.error("Okta /token response missing access_token: {}", resp);
            throw new UpstreamAuthException("No access_token in Okta /token response");
        }

        String accessToken = (String) resp.get("access_token");
        Number expiresIn = (Number) resp.getOrDefault("expires_in", 3600);
        expiresAtEpochSec = Instant.now().getEpochSecond() + expiresIn.intValue();
        cachedToken.set(accessToken);

        log.info("Fetched new access_token (len={}), expires in {}s", accessToken.length(), expiresIn);
        return accessToken;
    }
}
