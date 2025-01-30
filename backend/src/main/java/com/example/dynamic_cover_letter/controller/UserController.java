package com.example.dynamic_cover_letter.controller;

import com.example.dynamic_cover_letter.entity.User;
import com.example.dynamic_cover_letter.service.UserService;
import com.example.dynamic_cover_letter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;



@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint for user registration
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
    userService.registerUser(user);
    Map<String, String> response = new HashMap<>();
    response.put("message", "User registered successfully!");
    return ResponseEntity.ok(response);
}


    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
    User loggedInUser = userService.loginUser(user.getEmail(), user.getPassword());
    if (loggedInUser != null) {
        return ResponseEntity.ok("Login successful!");
    } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email or password!");
    }
    }
}
