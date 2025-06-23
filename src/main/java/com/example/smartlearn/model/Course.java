// Course.java
package com.example.smartlearn.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    private Long teacherId;
    @Column(name = "code")
    private String code;
    private String name;


    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer credit;
    private String term;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
