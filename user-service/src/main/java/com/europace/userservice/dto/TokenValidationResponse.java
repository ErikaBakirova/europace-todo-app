package com.europace.userservice.dto;

public class TokenValidationResponse {
    private boolean valid;
    private Long userId;
    private String username;
    private String message;

    public TokenValidationResponse() {}

    public TokenValidationResponse(boolean valid, Long userId, String username, String message) {
        this.valid = valid;
        this.userId = userId;
        this.username = username;
        this.message = message;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
