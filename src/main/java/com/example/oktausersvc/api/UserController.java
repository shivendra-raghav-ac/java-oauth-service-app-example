package com.example.oktausersvc.api;

import com.example.oktausersvc.dto.OktaUserResponse;
import com.example.oktausersvc.service.UserService;
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
     * GET /users/{id} — returns Okta user details by ID using M2M (client
     * credentials).
     */
    @GetMapping("/{id}")
    public ResponseEntity<OktaUserResponse> getById(@PathVariable String id) {
        log.info("Received request for user id={}", id);
        OktaUserResponse user = userService.getUser(id);
        log.info("Returning response for user id={} -> {}", id, user != null ? "found" : "null");
        return ResponseEntity.ok(user);
    }
}
