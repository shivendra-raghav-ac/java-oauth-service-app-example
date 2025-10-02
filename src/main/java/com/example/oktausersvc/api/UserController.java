package com.example.oktausersvc.api;

import com.example.oktausersvc.dto.OktaPagedUsersResponse;
import com.example.oktausersvc.dto.OktaUserResponse;
import com.example.oktausersvc.dto.OktaUserUpdateRequest;
import com.example.oktausersvc.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /users/{id} — returns Okta user details by ID using M2M Service
     * Application.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OktaUserResponse> getById(@PathVariable String id) {
        log.info("Received request for user id={}", id);
        OktaUserResponse user = userService.getUser(id);
        log.info("Returning response for user id={} -> {}", id, user != null ? "found" : "null");
        return ResponseEntity.ok(user);
    }

    /**
     * GET /users?limit=&after=
     * - limit (optional): page size (Okta default 20)
     * - after (optional): cursor from previous page
     */
    @GetMapping
    public ResponseEntity<OktaPagedUsersResponse> listUsers(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String after) {
        log.info("List users requested, limit={}, after={}", limit, after);
        OktaPagedUsersResponse page = userService.listUsers(limit, after);
        log.info("List users returned {} item(s), nextCursor={}",
                page != null && page.getItems() != null ? page.getItems().size() : 0,
                page != null ? page.getNextCursor() : null);
        return ResponseEntity.ok(page);
    }

    /**
     * PATCH /users/{id}/name — update only firstName and/or lastName.
     * Body: { "firstName": "...", "lastName": "..." }
     */
    @PatchMapping("/{id}")
    public ResponseEntity<OktaUserResponse> updateName(
            @PathVariable String id,
            @Valid @RequestBody OktaUserUpdateRequest req) {
        log.info("Update names for id={}, firstName? {}, lastName? {}",
                id, req.getFirstName() != null, req.getLastName() != null);
        OktaUserResponse updated = userService.updateUserNames(id, req.getFirstName(), req.getLastName());
        log.info("Updated names for id={} -> {}", id, updated != null ? "ok" : "null");
        return ResponseEntity.ok(updated);
    }
}
