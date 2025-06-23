package com.example.smartlearn.dto;

import com.example.smartlearn.model.User;
import lombok.Data;

@Data
public class LoginResponse {
    private Long userId;
    private String account;
    private String role;

    public LoginResponse(User user) {
        this.userId = user.getUserId();
        this.account = user.getAccount();
        this.role = user.getRole();
    }
}