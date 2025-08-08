package com.europace.todo.service.controller;

import com.europace.todo.service.dto.TodoRequest;
import com.europace.todo.service.entity.Todo;
import com.europace.todo.service.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private TodoService todoService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void createTodo_validToken_shouldReturn201() throws Exception {
        TodoRequest request = new TodoRequest("Learn Spring Boot");
        Todo savedTodo = new Todo("Learn Spring Boot", 1L);
        savedTodo.setId(1L);
        
        when(todoService.createTodo("Learn Spring Boot", "validtoken")).thenReturn(savedTodo);
        
        mockMvc.perform(post("/todos")
                .header("Authorization", "Bearer validtoken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Learn Spring Boot"))
                .andExpect(jsonPath("$.userId").value(1));
    }
    
    @Test
    void createTodo_noToken_shouldReturn401() throws Exception {
        TodoRequest request = new TodoRequest("Learn Spring Boot");
        
        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void createTodo_invalidToken_shouldReturn401() throws Exception {
        TodoRequest request = new TodoRequest("Learn Spring Boot");
        
        when(todoService.createTodo("Learn Spring Boot", "invalidtoken")).thenReturn(null);
        
        mockMvc.perform(post("/todos")
                .header("Authorization", "Bearer invalidtoken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void getTodos_validToken_shouldReturn200() throws Exception {
        List<Todo> todos = List.of(
                new Todo("Learn Spring Boot", 1L),
                new Todo("Build REST API", 1L)
        );
        todos.get(0).setId(1L);
        todos.get(1).setId(2L);
        
        when(todoService.getUserTodos("validtoken")).thenReturn(todos);
        
        mockMvc.perform(get("/todos")
                .header("Authorization", "Bearer validtoken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].text").value("Learn Spring Boot"))
                .andExpect(jsonPath("$[1].text").value("Build REST API"));
    }
    
    @Test
    void getTodos_noToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/todos"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void getTodos_invalidToken_shouldReturn401() throws Exception {
        when(todoService.getUserTodos("invalidtoken")).thenReturn(null);
        
        mockMvc.perform(get("/todos")
                .header("Authorization", "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized());
    }
}
