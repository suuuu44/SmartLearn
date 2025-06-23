package com.example.smartlearn.service;

import com.example.smartlearn.dto.LoginRequest;
import com.example.smartlearn.dto.LoginResponse;
import com.example.smartlearn.model.User;
import com.example.smartlearn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public LoginResponse authenticate(LoginRequest request) {
        // 添加日志
        System.out.println("接收登录请求: " + request.getAccount() + ", 角色: " + request.getRole());

        // 添加角色条件查询
        User user = userRepository.findByAccountAndRole(request.getAccount(), request.getRole())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号不存在"));

        System.out.println("数据库查询结果: " + user.getAccount());

        // 密码验证
        if (!request.getPassword().equals(user.getPasswordHash())) {
            System.out.println("密码错误");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "密码错误");
        }

        // 返回登录信息
        return new LoginResponse(user);
    }
}