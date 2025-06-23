package com.example.smartlearn.dto;

import lombok.Data;

@Data
public class CourseRequest {
    private Long teacherId;
    private String code;
    private String name;
    private String description;
    private Integer credit;
    private String term;

}