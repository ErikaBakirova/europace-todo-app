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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "User API", description = "Endpoints for user registration, login, and token verification")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user and returns a JWT token",
        responses = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
        }
    )
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
    @Operation(
        summary = "Login existing user",
        description = "Authenticates a user and returns a JWT token",
        responses = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
        }
    )
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
    @Operation(
        summary = "Verify JWT token",
        description = "Validates the given JWT token and returns user info if valid",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token")
        }
    )
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
