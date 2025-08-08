package com.europace.userservice.controller;

import com.europace.userservice.dto.*;
import com.europace.userservice.entity.User;
import com.europace.userservice.service.JwtService;
import com.europace.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    
    private final UserService userService;
    private final JwtService jwtService;
    
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request.getUsername(), request.getPassword());
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        String token = jwtService.generateToken(user.getId());
        AuthResponse response = new AuthResponse(token, user.getUsername(), user.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.authenticateUser(request.getUsername(), request.getPassword());
        
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        User user = userService.findByUsername(request.getUsername());
        AuthResponse response = new AuthResponse(token, user.getUsername(), user.getId());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/token")
    public ResponseEntity<TokenValidationResponse> verifyToken(@RequestBody TokenRequest request) {
        User user = userService.verifyToken(request.getToken());
        
        if (user == null) {
            TokenValidationResponse response = new TokenValidationResponse(false, null, null, "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        TokenValidationResponse response = new TokenValidationResponse(true, user.getId(), user.getUsername(), "Token valid");
        return ResponseEntity.ok(response);
    }
}
