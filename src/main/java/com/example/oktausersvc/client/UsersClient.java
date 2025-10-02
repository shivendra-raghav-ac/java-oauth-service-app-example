package com.example.oktausersvc.client;

import com.example.oktausersvc.config.AppProperties;
import com.example.oktausersvc.dto.OktaPagedUsersResponse;
import com.example.oktausersvc.dto.OktaUserResponse;
import com.example.oktausersvc.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class UsersClient {

  private static final Logger log = LoggerFactory.getLogger(UsersClient.class);

  private final WebClient webClient;
  private final String baseUrl;

  public UsersClient(WebClient webClient, AppProperties props) {
    this.webClient = webClient;
    var okta = props.getOkta();
    this.baseUrl = okta.getDomain() + okta.getApiBase();
  }

  public OktaUserResponse getUserById(String id, String bearerToken) {
    final String url = baseUrl + "/users/" + id;

    return webClient.get()
        .uri(url)
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
        .retrieve()
        .onStatus(status -> status == HttpStatus.NOT_FOUND,
            resp -> Mono.error(new UserNotFoundException("User not found: " + id)))
        .onStatus(status -> status.isError(),
            resp -> resp.bodyToMono(String.class).flatMap(body -> {
              log.error("Okta getUserById error [{}]: {}", resp.statusCode(), body);
              return Mono.error(new RuntimeException("Okta error: " + body));
            }))
        .bodyToMono(OktaUserResponse.class)
        .block();
  }

  public OktaPagedUsersResponse listUsers(Integer limit, String after, String bearerToken) {
    final StringBuilder url = new StringBuilder(baseUrl).append("/users");
    boolean first = true;
    if (limit != null) {
      url.append(first ? "?" : "&").append("limit=").append(limit);
      first = false;
    }
    if (after != null && !after.isBlank()) {
      url.append(first ? "?" : "&").append("after=").append(after);
    }

    ResponseEntity<List<OktaUserResponse>> entity = webClient.get()
        .uri(url.toString())
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
        .retrieve()
        .onStatus(status -> status.isError(),
            resp -> resp.bodyToMono(String.class).flatMap(body -> {
              log.error("Okta listUsers error [{}]: {}", resp.statusCode(), body);
              return Mono.error(new RuntimeException("Okta list error: " + body));
            }))
        .toEntityList(OktaUserResponse.class)
        .block();

    List<OktaUserResponse> items = (entity != null && entity.getBody() != null) ? entity.getBody() : List.of();
    String next = (entity != null) ? extractAfterFromLinkHeader(entity.getHeaders().getFirst(HttpHeaders.LINK)) : null;
    return new OktaPagedUsersResponse(items, next);
  }

  public OktaUserResponse updateUserNames(String id, String firstName, String lastName, String bearerToken) {
    final String url = baseUrl + "/users/" + id;

    // Build minimal payload: { "profile": { "firstName": "...", "lastName": "..." }
    // }
    var profile = new java.util.LinkedHashMap<String, Object>();
    if (firstName != null && !firstName.isBlank())
      profile.put("firstName", firstName);
    if (lastName != null && !lastName.isBlank())
      profile.put("lastName", lastName);

    if (profile.isEmpty()) {
      throw new IllegalArgumentException("At least one of firstName or lastName must be provided");
    }

    var payload = java.util.Map.of("profile", profile);

    // Log the exact JSON we'll send (debug level)
    try {
      String json = new ObjectMapper().writeValueAsString(payload);
      log.debug("Okta updateUserNames payload for {}: {}", id, json);
    } catch (Exception ignore) {
    }

    return webClient.post()
        .uri(url)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
        .bodyValue(payload)
        .retrieve()
        .onStatus(status -> status == HttpStatus.NOT_FOUND,
            resp -> Mono.error(new UserNotFoundException("User not found: " + id)))
        .onStatus(status -> status.isError(),
            resp -> resp.bodyToMono(String.class).flatMap(errBody -> {
              log.error("Okta updateUserNames error [{}]: {}", resp.statusCode(), errBody);
              return Mono.error(new RuntimeException("Okta update error: " + errBody));
            }))
        .bodyToMono(OktaUserResponse.class)
        .block();
  }

  private String extractAfterFromLinkHeader(String linkHeader) {
    if (linkHeader == null || linkHeader.isBlank())
      return null;
    for (String part : linkHeader.split(",")) {
      String seg = part.trim();
      if (seg.contains("rel=\"next\"")) {
        int start = seg.indexOf('<');
        int end = seg.indexOf('>');
        if (start >= 0 && end > start) {
          String url = seg.substring(start + 1, end);
          int idx = url.indexOf("after=");
          if (idx >= 0) {
            String after = url.substring(idx + 6);
            int amp = after.indexOf('&');
            if (amp >= 0)
              after = after.substring(0, amp);
            return after;
          }
        }
      }
    }
    return null;
  }
}
