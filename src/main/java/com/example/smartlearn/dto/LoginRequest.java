package com.example.smartlearn.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String account;
    private String password;
    private String role; // 添加角色字段
}