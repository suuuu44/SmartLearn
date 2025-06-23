package com.example.smartlearn.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseResponse {
    private Long courseId;
    private String code;
    private String name;
    private String description;
    private Integer credit;
    private String term;
    private LocalDateTime createdAt;

    // Getters and Setters
}