package com.europace.userservice.dto;


import jakarta.validation.constraints.NotBlank;

public class TokenRequest {
    @NotBlank(message = "Token is required")
    private String token;
    
    public TokenRequest() {}
    
    public TokenRequest(String token) {
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}
