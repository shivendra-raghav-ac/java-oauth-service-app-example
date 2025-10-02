package com.example.oktausersvc.service;

import com.example.oktausersvc.client.TokenClient;
import com.example.oktausersvc.client.UsersClient;
import com.example.oktausersvc.dto.OktaPagedUsersResponse;
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

    /** Fetch single user by ID */
    public OktaUserResponse getUser(String id) {
        log.debug("Fetching access token for user lookup");
        String token = tokenClient.getAccessToken();
        log.debug("Got access token (length={})", token != null ? token.length() : 0);
        log.info("Calling Okta /users/{} endpoint", id);
        OktaUserResponse resp = usersClient.getUserById(id, token);
        log.info("Okta returned user {} for id={}", resp != null ? resp.getId() : "null", id);
        return resp;
    }

    /** List users with optional paging */
    public OktaPagedUsersResponse listUsers(Integer limit, String after) {
        log.debug("Fetching access token for listUsers(limit={}, after={})", limit, after);
        String token = tokenClient.getAccessToken();
        log.debug("Got access token (length={})", token != null ? token.length() : 0);
        OktaPagedUsersResponse page = usersClient.listUsers(limit, after, token);
        log.info("Okta returned {} user(s), nextCursor={}",
                page != null && page.getItems() != null ? page.getItems().size() : 0,
                page != null ? page.getNextCursor() : null);
        return page;
    }

    /** Update only firstName / lastName of a user */
    public OktaUserResponse updateUserNames(String id, String firstName, String lastName) {
        if ((firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank())) {
            throw new IllegalArgumentException("At least one of firstName or lastName must be provided");
        }
        log.debug("Fetching access token for updateUserNames(id={})", id);
        String token = tokenClient.getAccessToken();
        log.debug("Got access token (length={})", token != null ? token.length() : 0);
        log.info("Calling Okta to update names for id={}, firstName? {}, lastName? {}",
                id, firstName != null, lastName != null);
        OktaUserResponse updated = usersClient.updateUserNames(id, firstName, lastName, token);
        log.info("Okta updated user id={} -> {}", id, updated != null ? "ok" : "null");
        return updated;
    }
}
