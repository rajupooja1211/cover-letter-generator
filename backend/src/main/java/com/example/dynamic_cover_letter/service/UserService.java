package com.example.dynamic_cover_letter.service;

import com.example.dynamic_cover_letter.entity.User;
import com.example.dynamic_cover_letter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public User loginUser(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
}
