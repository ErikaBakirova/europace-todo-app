package com.europace.todo.service.controller;

import com.europace.todo.service.dto.TodoRequest;
import com.europace.todo.service.dto.TodoResponse;
import com.europace.todo.service.entity.Todo;
import com.europace.todo.service.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TodoController {
    
    private final TodoService todoService;
    
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }
    
    @PostMapping("/todos")
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request,
                                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = extractToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Todo todo = todoService.createTodo(request.getText(), token);
        if (todo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        TodoResponse response = new TodoResponse(todo.getId(), todo.getText(), todo.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/todos")
    public ResponseEntity<List<TodoResponse>> getTodos(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = extractToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Todo> todos = todoService.getUserTodos(token);
        if (todos == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<TodoResponse> response = todos.stream()
                .map(todo -> new TodoResponse(todo.getId(), todo.getText(), todo.getUserId()))
                .toList();
        
        return ResponseEntity.ok(response);
    }
    
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
