package com.example.smartlearn.repository;

import com.example.smartlearn.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Integer>,JpaSpecificationExecutor<Resource> {

    List<Resource> findByCourseId(Integer courseId);

    List<Resource> findByTaskId(Integer taskId);

    List<Resource> findByType(Resource.ResourceType type);

    List<Resource> findByNameContaining(String keyword);

    @Query("SELECT r FROM Resource r WHERE r.name = :name AND r.courseId = :courseId")
    Optional<Resource> findByCourseAndName(@Param("courseId") Integer courseId,
                                           @Param("name") String name);


    @Query("SELECT r FROM Resource r WHERE r.courseId = :courseId AND r.type = :type")
    List<Resource> findByCourseIdAndType(@Param("courseId") Integer courseId,
                                         @Param("type") Resource.ResourceType type);

    @Query("SELECT r FROM Resource r WHERE r.courseId = :courseId AND r.type = :type AND r.name LIKE %:keyword%")
    List<Resource> findByCourseIdAndTypeAndNameContaining(@Param("courseId") Integer courseId,
                                                          @Param("type") Resource.ResourceType type,
                                                          @Param("keyword") String keyword);

    @Query("SELECT r FROM Resource r WHERE r.type = :type AND r.courseId = :courseId")
    List<Resource> findByTypeAndCourseId(@Param("type") Resource.ResourceType type,
                                         @Param("courseId") Integer courseId);


    @Query("SELECT r FROM Resource r WHERE " +
            "(:courseIds IS NULL OR r.courseId IN :courseIds) AND " +
            "(:type IS NULL OR r.type = :type) AND " +
            "(:keyword IS NULL OR r.name LIKE %:keyword%)")
    List<Resource> findByCourseIdsAndTypeAndKeyword(
            @Param("courseIds") List<Integer> courseIds,
            @Param("type") Resource.ResourceType type,
            @Param("keyword") String keyword
    );


}