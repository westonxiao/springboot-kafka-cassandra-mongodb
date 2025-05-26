package com.healthcare.account.controller;

import com.healthcare.account.exception.InvalidTokenException;
import com.healthcare.account.model.User;
import com.healthcare.account.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping
    public User createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        kafkaTemplate.send("user-events", "USER_CREATED:" + savedUser.getId());
        return savedUser;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Autowired
    private final RestTemplate restTemplate;
    private final String authServiceUrl = "http://localhost:8081/auth/validate";

    @Autowired
    public UserController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getProfile(
            @RequestHeader("Authorization") String token) {

        // 1. Validate token with Auth Service
        boolean isValid = validateTokenWithAuthService(token);

        if (!isValid) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        // 2. Return profile data (simplified example)
        return ResponseEntity.ok("Protected profile data for user");
    }

    private boolean validateTokenWithAuthService(String token) {
        try {
            // Prepare headers with the token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            // Call Auth Service
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    authServiceUrl,
                    HttpMethod.GET,
                    entity,
                    Boolean.class
            );

            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Auth Service unavailable", e);
        }
    }
}