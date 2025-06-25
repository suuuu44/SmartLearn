// CourseService.java
package com.example.smartlearn.service;

import com.example.smartlearn.exception.ResourceNotFoundException;
import com.example.smartlearn.model.Course;
import com.example.smartlearn.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }


    @Transactional
    public Course updateCourse(Long courseId, Course updatedCourse) {
        return courseRepository.findById(courseId)
                .map(course -> {
                    // 保留原始课程编号，不更新它
                    // course.setCode(updatedCourse.getCode()); // 移除这一行

                    // 只更新这些字段
                    course.setName(updatedCourse.getName());
                    course.setDescription(updatedCourse.getDescription());
                    course.setCredit(updatedCourse.getCredit());
                    course.setTerm(updatedCourse.getTerm());

                    return courseRepository.save(course);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }
    public List<Course> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }



}

