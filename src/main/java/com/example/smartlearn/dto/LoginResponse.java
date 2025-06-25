package com.example.smartlearn.dto;

import com.example.smartlearn.model.User;
import lombok.Data;

@Data
public class LoginResponse {
    private Long userId;
    private String account;
    private String role;
    private Long teacherId;
    private Long studentId;
    private String token;
    public LoginResponse(User user) {
        this.userId = user.getUserId();
        this.account = user.getAccount();
        this.role = user.getRole();
        this.token = ""; // 初始化为空字符串
        if (role.equals("teacher")){
            this.teacherId=userId;
        }

    }
    public LoginResponse(User user, String token) {
        this(user);
        this.token = token;
    }
}