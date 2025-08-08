package com.europace.todo.service.service;

import com.europace.todo.service.entity.Todo;
import com.europace.todo.service.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {
    
    @Mock
    private TodoRepository todoRepository;
    
    @Mock
    private JwtService jwtService;
    
    @InjectMocks
    private TodoService todoService;
    
    @Test
    void createTodo_validToken_shouldReturnTodo() {
        when(jwtService.validateToken("validtoken")).thenReturn(true);
        when(jwtService.extractUserId("validtoken")).thenReturn(1L);
        
        Todo savedTodo = new Todo("Learn Spring Boot", 1L);
        savedTodo.setId(1L);
        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);
        
        Todo result = todoService.createTodo("Learn Spring Boot", "validtoken");
        
        assertNotNull(result);
        assertEquals("Learn Spring Boot", result.getText());
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getId());
        verify(todoRepository).save(any(Todo.class));
    }
    
    @Test
    void createTodo_invalidToken_shouldReturnNull() {
        when(jwtService.validateToken("invalidtoken")).thenReturn(false);
        
        Todo result = todoService.createTodo("Learn Spring Boot", "invalidtoken");
        
        assertNull(result);
        verify(todoRepository, never()).save(any(Todo.class));
    }
    
    @Test
    void getUserTodos_validToken_shouldReturnTodos() {
        when(jwtService.validateToken("validtoken")).thenReturn(true);
        when(jwtService.extractUserId("validtoken")).thenReturn(1L);
        
        List<Todo> mockTodos = List.of(
                new Todo("Learn Spring Boot", 1L),
                new Todo("Build REST API", 1L)
        );
        when(todoRepository.findByUserId(1L)).thenReturn(mockTodos);
        
        List<Todo> result = todoService.getUserTodos("validtoken");
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Learn Spring Boot", result.get(0).getText());
        assertEquals("Build REST API", result.get(1).getText());
    }
    
    @Test
    void getUserTodos_invalidToken_shouldReturnNull() {
        when(jwtService.validateToken("invalidtoken")).thenReturn(false);
        
        List<Todo> result = todoService.getUserTodos("invalidtoken");
        
        assertNull(result);
        verify(todoRepository, never()).findByUserId(any());
    }
}
