package com.example.oktausersvc.service;

import com.example.oktausersvc.client.TokenClient;
import com.example.oktausersvc.client.UsersClient;
import com.example.oktausersvc.dto.OktaUserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final TokenClient tokenClient;
    private final UsersClient usersClient;

    public UserService(TokenClient tokenClient, UsersClient usersClient) {
        this.tokenClient = tokenClient;
        this.usersClient = usersClient;
    }

    public OktaUserResponse getUser(String id) {
        log.debug("Fetching access token for user lookup");
        String token = tokenClient.getAccessToken();
        log.debug("Got access token (length={})", token != null ? token.length() : 0);
        log.info("Calling Okta /users/{} endpoint", id);
        OktaUserResponse resp = usersClient.getUserById(id, token);
        log.info("Okta returned user {} for id={}", resp != null ? resp.getId() : "null", id);
        return resp;
    }
}
