package com.europace.userservice.service;

import com.europace.userservice.entity.User;
import com.europace.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final JwtService jwtService;
    
    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }
    
    public User registerUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            return null;
        }
        
        User user = new User(username, password);
        return userRepository.save(user);
    }
    
    public String authenticateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .map(user -> jwtService.generateToken(user.getId()))
                .orElse(null);
    }
    
    public User verifyToken(String token) {
        if (!jwtService.validateToken(token)) {
            return null;
        }
        
        Long userId = jwtService.extractUserId(token);
        return userRepository.findById(userId).orElse(null);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
