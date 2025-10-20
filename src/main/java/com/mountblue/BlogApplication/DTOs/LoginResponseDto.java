package com.mountblue.BlogApplication.DTOs;

import com.mountblue.BlogApplication.entity.User;

public class LoginResponseDto {
    private String username;
    private String email;
    private boolean isAdmin;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public static LoginResponseDto toDto(User user){
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setAdmin(user.isAdmin());
        loginResponseDto.setUsername(user.getName());
        loginResponseDto.setEmail(user.getEmail());
        return loginResponseDto;
    }
}
