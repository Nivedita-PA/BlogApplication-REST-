package com.mountblue.BlogApplication.service;

import com.mountblue.BlogApplication.DTOs.LoginResponseDto;
import com.mountblue.BlogApplication.entity.User;
import com.mountblue.BlogApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        Optional<User> user1 = userRepository.findByName(user.getName());
        Optional<User> user2 = userRepository.findByEmail(user.getEmail());
        if (user1.isPresent() || user2.isPresent()) throw new RuntimeException("User is already present!");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<LoginResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<LoginResponseDto> dtos = new ArrayList<>();
        for(User user : users){
            dtos.add(LoginResponseDto.toDto(user));
        }
        return dtos;
    }

    public User loginUser(String userName, String password) {
        Optional<User> user = userRepository.findByName(userName);
        if (user.isPresent()) {
            if (passwordEncoder.matches(password, user.get().getPassword())) return user.get();
        }
        throw new RuntimeException("Invalid credentials");
   }

   public User findUserByName(String name){
        return userRepository.findByName(name).orElseThrow(()-> new RuntimeException("User not found"));
   }
}
