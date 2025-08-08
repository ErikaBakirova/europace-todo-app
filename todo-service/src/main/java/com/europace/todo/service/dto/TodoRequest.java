package com.europace.todo.service.dto;

public class TodoRequest {
    private String text;
    
    public TodoRequest() {}
    
    public TodoRequest(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
}
