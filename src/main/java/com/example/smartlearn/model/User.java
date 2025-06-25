package com.example.smartlearn.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId; // 修改为驼峰命名

    @Column(unique = true, nullable = false)
    private String account;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash; // 修改为驼峰命名

    @Column(name = "role", nullable = false)
    private String role; // 修改为字符串类型

    public enum Role {
        teacher, student // 保持小写与数据库一致
    }


}