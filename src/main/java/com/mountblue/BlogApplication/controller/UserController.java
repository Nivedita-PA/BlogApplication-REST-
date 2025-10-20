package com.mountblue.BlogApplication.controller;

import com.mountblue.BlogApplication.DTOs.LoginRequestDto;
import com.mountblue.BlogApplication.DTOs.LoginResponseDto;
import com.mountblue.BlogApplication.entity.User;
import com.mountblue.BlogApplication.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        try {
            userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "User registered successfully!",
                    "username", user.getName(), "email", user.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/loginUser")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            User user = userService.loginUser(loginRequestDto.getUsername(), loginRequestDto.getPassword());
            return ResponseEntity.ok().body(Map.of("logged in", LoginResponseDto.toDto(user)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully!!!");
    }


    @GetMapping("/all")
    public ResponseEntity<List<LoginResponseDto>> getAllUsers() {
        List<LoginResponseDto> users = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
}
