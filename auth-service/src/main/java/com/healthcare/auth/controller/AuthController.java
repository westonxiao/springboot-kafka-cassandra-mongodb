package com.healthcare.auth.controller;

import com.healthcare.auth.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {
        // In a real app, validate credentials against database
        return jwtUtil.generateToken(username);
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestHeader("Authorization") String token) {
        // Your JWT validation logic here
        return jwtUtil.validateToken(token);
    }
}