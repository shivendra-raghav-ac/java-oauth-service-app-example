package com.example.oktausersvc.client;

import com.example.oktausersvc.config.AppProperties;
import com.example.oktausersvc.dto.OktaUserResponse;
import com.example.oktausersvc.exception.UserNotFoundException;

import java.util.function.Supplier;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UsersClient {

  private final WebClient webClient;
  private final String baseUrl;

  public UsersClient(WebClient webClient, AppProperties props) {
    this.webClient = webClient;
    var okta = props.getOkta();
    this.baseUrl = okta.getDomain() + okta.getApiBase();
  }

  @SuppressWarnings("unchecked")
  public OktaUserResponse getUserById(String id, String bearerToken) {
    return webClient.get()
        .uri(baseUrl + "/users/{id}", id)
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
        .retrieve()
        .onStatus(status -> status == HttpStatus.NOT_FOUND,
            resp -> Mono.error((Supplier<? extends Throwable>) new UserNotFoundException()))
        .onStatus(status -> status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN,
            resp -> Mono.error(new RuntimeException("Unauthorized/Forbidden calling Okta /users/" + id)))
        .bodyToMono(OktaUserResponse.class)
        .block();
  }
}
