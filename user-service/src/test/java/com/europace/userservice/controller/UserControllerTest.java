package com.europace.userservice.controller;

import com.europace.userservice.controller.UserController;
import com.europace.userservice.dto.LoginRequest;
import com.europace.userservice.dto.RegisterRequest;
import com.europace.userservice.dto.TokenRequest;
import com.europace.userservice.entity.User;
import com.europace.userservice.service.JwtService;
import com.europace.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_newUser_shouldReturn201() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "password123");
        User mockUser = new User("testuser", "password123");
        mockUser.setId(1L);
        
        when(userService.registerUser("testuser", "password123")).thenReturn(mockUser);
        when(jwtService.generateToken(1L)).thenReturn("token123");
        
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.userId").value(1));
    }
    
    @Test
    void register_existingUser_shouldReturn409() throws Exception {
        RegisterRequest request = new RegisterRequest("existing", "password123");
        
        when(userService.registerUser("existing", "password123")).thenReturn(null);
        
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
    
    @Test
    void login_validCredentials_shouldReturn200WithToken() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password123");
        User mockUser = new User("testuser", "password123");
        mockUser.setId(1L);
        
        when(userService.authenticateUser("testuser", "password123")).thenReturn("token123");
        when(userService.findByUsername("testuser")).thenReturn(mockUser);
        
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.userId").value(1));
    }
    
    @Test
    void login_invalidCredentials_shouldReturn401() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "wrong");
        
        when(userService.authenticateUser("testuser", "wrong")).thenReturn(null);
        
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void verifyToken_validToken_shouldReturn200() throws Exception {
        TokenRequest tokenRequest = new TokenRequest("valid.token.here");
        User mockUser = new User("testuser", "password123");
        mockUser.setId(1L);
        
        when(userService.verifyToken("valid.token.here")).thenReturn(mockUser);
        
        mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }
    
    @Test
    void verifyToken_invalidToken_shouldReturn401() throws Exception {
        TokenRequest tokenRequest = new TokenRequest("invalid.token.here");
        
        when(userService.verifyToken("invalid.token.here")).thenReturn(null);
        
        mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));
    }
}
