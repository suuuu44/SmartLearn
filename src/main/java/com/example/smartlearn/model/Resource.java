package com.example.smartlearn.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "course_id", insertable = false, updatable = false)
    private Integer courseId;

    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "name", length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "ENUM('ppt','pdf','video','doc')")
    private ResourceType type;

    @Column(name = "url", length = 255)
    private String url;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 资源类型枚举
    public enum ResourceType {
        ppt, pdf, video, doc
    }

    public String getFileExtension() {
        switch (type) {
            case ppt: return ".pptx";
            case pdf: return ".pdf";
            case video: return ".mp4";
            case doc: return ".docx";
            default: return "";
        }
    }

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

}