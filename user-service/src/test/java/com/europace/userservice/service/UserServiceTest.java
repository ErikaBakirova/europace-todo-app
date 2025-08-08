package com.europace.userservice.service;

import com.europace.userservice.entity.User;
import com.europace.userservice.repository.UserRepository;
import com.europace.userservice.service.JwtService;
import com.europace.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private JwtService jwtService;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void registerUser_newUser_shouldReturnUser() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        
        User result = userService.registerUser("newuser", "password123");
        
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("password123", result.getPassword());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void registerUser_existingUser_shouldReturnNull() {
        when(userRepository.existsByUsername("existing")).thenReturn(true);
        
        User result = userService.registerUser("existing", "password123");
        
        assertNull(result);
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void authenticateUser_validCredentials_shouldReturnToken() {
        User user = new User("testuser", "password123");
        user.setId(123L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(123L)).thenReturn("token123");
        
        String token = userService.authenticateUser("testuser", "password123");
        
        assertEquals("token123", token);
    }
    
    @Test
    void authenticateUser_wrongPassword_shouldReturnNull() {
        User user = new User("testuser", "password123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        
        String token = userService.authenticateUser("testuser", "wrongpassword");
        
        assertNull(token);
    }
    
    @Test
    void authenticateUser_userNotFound_shouldReturnNull() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        String token = userService.authenticateUser("nonexistent", "password123");
        
        assertNull(token);
    }
    
    @Test
    void verifyToken_validToken_shouldReturnUser() {
        when(jwtService.validateToken("valid.token")).thenReturn(true);
        when(jwtService.extractUserId("valid.token")).thenReturn(123L);
        User user = new User("testuser", "password123");
        user.setId(123L);
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));
        
        User result = userService.verifyToken("valid.token");
        
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }
    
    @Test
    void verifyToken_invalidToken_shouldReturnNull() {
        when(jwtService.validateToken("invalid.token")).thenReturn(false);
        
        User result = userService.verifyToken("invalid.token");
        
        assertNull(result);
    }
}
