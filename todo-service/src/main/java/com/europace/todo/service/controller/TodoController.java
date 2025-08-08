package com.europace.todo.service.controller;

import com.europace.todo.service.dto.TodoRequest;
import com.europace.todo.service.dto.TodoResponse;
import com.europace.todo.service.entity.Todo;
import com.europace.todo.service.service.TodoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Todo API", description = "Endpoints for managing todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping("/todos")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Create a new todo",
        description = "Creates a new todo item for the authenticated user",
        responses = {
            @ApiResponse(responseCode = "201", description = "Todo successfully created"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authorization token")
        }
    )
    public ResponseEntity<TodoResponse> createTodo(
            @Valid @RequestBody TodoRequest request,
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Get all todos for authenticated user",
        description = "Retrieves all todo items belonging to the authenticated user",
        responses = {
            @ApiResponse(responseCode = "200", description = "Todos successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authorization token")
        }
    )
    public ResponseEntity<List<TodoResponse>> getTodos(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
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
