package com.europace.todo.service.dto;

import jakarta.validation.constraints.NotBlank;

public class TodoResponse {
    @NotBlank(message = "Todo text is required")
    private Long id;
    private String text;
    private Long userId;
    
    public TodoResponse() {}
    
    public TodoResponse(Long id, String text, Long userId) {
        this.id = id;
        this.text = text;
        this.userId = userId;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
