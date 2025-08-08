package com.europace.todo.service.service;

import com.europace.todo.service.entity.Todo;
import com.europace.todo.service.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    
    private final TodoRepository todoRepository;
    private final JwtService jwtService;
    
    public TodoService(TodoRepository todoRepository, JwtService jwtService) {
        this.todoRepository = todoRepository;
        this.jwtService = jwtService;
    }
    
    public Todo createTodo(String text, String token) {
        if (!jwtService.validateToken(token)) {
            return null;
        }
        
        Long userId = jwtService.extractUserId(token);
        if (userId == null) {
            return null;
        }
        
        Todo todo = new Todo(text, userId);
        return todoRepository.save(todo);
    }
    
    public List<Todo> getUserTodos(String token) {
        if (!jwtService.validateToken(token)) {
            return null;
        }
        
        Long userId = jwtService.extractUserId(token);
        if (userId == null) {
            return null;
        }
        
        return todoRepository.findByUserId(userId);
    }
}
