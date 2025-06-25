package com.example.smartlearn.service;

import com.example.smartlearn.exception.ResourceNotFoundException;
import com.example.smartlearn.model.Resource;
import com.example.smartlearn.repository.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; // 正确的导入
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import com.example.smartlearn.repository.CourseRepository;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.criteria.Predicate;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Value("${file.max-size:104857600}") // 默认100MB
    private long maxFileSize;
    private final CourseRepository courseRepository;
    public ResourceService(ResourceRepository resourceRepository, CourseRepository courseRepository) {
        this.resourceRepository = resourceRepository;
        this.courseRepository = courseRepository;
    }

    // 增：上传新资源
    public Resource uploadResource(Integer courseId, String name,
                                   Resource.ResourceType type,
                                   MultipartFile file,
                                   String description) throws IOException {
        // 生成唯一文件名
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // 根据类型确定子目录
        String subDir = type.name().toLowerCase();

        // 创建目标路径 (使用相对路径)
        Path targetLocation = Paths.get(uploadDir)
                .resolve("courses")
                .resolve(courseId.toString())
                .resolve("resources")
                .resolve(subDir);

        // 确保目录存在
        Files.createDirectories(targetLocation);

        // 创建文件路径
        Path filePath = targetLocation.resolve(filename);

        // 保存文件
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 创建资源对象
        Resource resource = new Resource();
        resource.setCourseId(courseId);
        resource.setName(name);
        resource.setType(type);

        // 创建相对路径 (作为URL)
        String relativePath = "courses/" + courseId + "/resources/" + subDir + "/" + filename;

        // 设置URL为完整的访问路径
        resource.setUrl("/uploads/" + relativePath);

        resource.setCreatedAt(LocalDateTime.now());
        resource.setDescription(description);

        // 保存到数据库
        return resourceRepository.save(resource);
    }

    //删除资源
    /**
     * 删除资源（包含物理文件和数据库记录）
     *
     * @param resourceId 资源ID
     * @throws ResourceNotFoundException 如果资源未找到
     * @throws IOException 如果删除文件失败
     */
    public void deleteResource(Integer resourceId) throws ResourceNotFoundException, IOException {
        // 1. 查找资源
        Resource resource = getResourceById(resourceId);

        log.info("开始删除资源: ID={}, 名称={}", resourceId, resource.getName());

        // 2. 获取文件路径
        String url = resource.getUrl();
        String relativePath = url.startsWith("/uploads/") ?
                url.substring("/uploads/".length()) :
                url;
        Path filePath = Paths.get(uploadDir, relativePath);

        // 3. 删除物理文件
        if (Files.exists(filePath)) {
            log.info("删除物理文件: 路径={}", filePath);
            Files.delete(filePath);
        } else {
            log.warn("物理文件不存在: 路径={}", filePath);
        }

        // 4. 删除数据库记录
        log.info("删除数据库记录: ID={}", resourceId);
        resourceRepository.delete(resource);
    }



    // 改：更新资源信息
    public Resource updateResource(Integer resourceId, String name, String description) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        // 调试信息
        System.out.println("更新前资源信息:");
        System.out.println("ID: " + resource.getResourceId());
        System.out.println("名称: " + resource.getName());
        System.out.println("描述: " + resource.getDescription());

        // 添加空值检查和修剪空白字符
        if (name != null && !name.trim().isEmpty()) {
            resource.setName(name.trim());
        } else {
            // 如果不提供名称，使用原始名称
            name = resource.getName();
        }

        if (description != null) {
            // 允许空描述
            resource.setDescription(description.trim());
        } else {
            // 如果不提供描述，使用原始描述
            description = resource.getDescription();
        }

        // 保存前打印更新后的值
        System.out.println("更新后资源信息:");
        System.out.println("名称: " + name);
        System.out.println("描述: " + description);

        // 确保保存并返回更新后的对象
        Resource updated = resourceRepository.save(resource);

        System.out.println("数据库返回的更新资源:");
        System.out.println("名称: " + updated.getName());
        System.out.println("描述: " + updated.getDescription());

        return updated;
    }
    // 更新资源文件
    public Resource updateResourceFile(Integer resourceId, MultipartFile file) throws IOException {
        // 获取现有资源
        Resource resource = getResourceById(resourceId);

        // 获取旧文件路径
        String oldUrl = resource.getUrl();
        String oldRelativePath = oldUrl.startsWith("/uploads/") ?
                oldUrl.substring("/uploads/".length()) : oldUrl;
        Path oldFilePath = Paths.get(uploadDir).resolve(oldRelativePath);

        // 删除旧文件
        Files.deleteIfExists(oldFilePath);

        // 生成新文件名
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // 根据类型确定子目录
        String subDir = resource.getType().name().toLowerCase();

        // 创建新文件路径
        Path newFilePath = Paths.get(uploadDir)
                .resolve("courses")
                .resolve(resource.getCourseId().toString())
                .resolve("resources")
                .resolve(subDir)
                .resolve(filename);

        // 确保目录存在
        Files.createDirectories(newFilePath.getParent());

        // 保存新文件
        Files.copy(file.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

        // 创建相对路径 (作为URL)
        String relativePath = "courses/" + resource.getCourseId() + "/resources/" + subDir + "/" + filename;

        // 更新资源信息
        resource.setUrl("/uploads/" + relativePath);

        // 保存到数据库
        return resourceRepository.save(resource);
    }

    // 查：根据条件查询资源
    public List<Resource> getResources(Integer courseId, Resource.ResourceType type, String keyword) {
        if (courseId != null && type != null && keyword != null) {
            return resourceRepository.findByCourseIdAndTypeAndNameContaining(courseId, type, keyword);
        } else if (courseId != null && type != null) {
            return resourceRepository.findByCourseIdAndType(courseId, type);
        } else if (courseId != null) {
            return resourceRepository.findByCourseId(courseId);
        } else if (type != null) {
            return resourceRepository.findByType(type);
        } else if (keyword != null) {
            return resourceRepository.findByNameContaining(keyword);
        } else {
            return resourceRepository.findAll();
        }
    }

    // 添加文件分类方法
    public List<Resource> getResourcesByCategory(String category, Integer courseId) {
        return resourceRepository.findByTypeAndCourseId(Resource.ResourceType.valueOf(category), courseId);
    }

    // 根据ID获取资源
    public Resource getResourceById(Integer resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    public InputStream downloadResource(Integer resourceId) throws IOException {
        Resource resource = getResourceById(resourceId);

        // 从URL中提取相对路径
        String url = resource.getUrl();
        String relativePath = url.startsWith("/uploads/") ? url.substring("/uploads/".length()) : url;

        // 构建完整文件路径
        Path fullPath = Paths.get(uploadDir).resolve(relativePath);

        // 返回文件流
        return new FileInputStream(fullPath.toFile());
    }

    // 获取教师的所有资源
    public List<Resource> getTeacherResources(Integer teacherId,
                                              Integer courseId,
                                              Resource.ResourceType type,
                                              String keyword) {

        // 1. 获取该教师的所有课程ID
        List<Integer> courseIds = courseRepository.findIdsByTeacherId(teacherId);

        if (courseIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 如果指定了课程ID，确保该课程属于该教师
        if (courseId != null) {
            if (!courseIds.contains(courseId)) {
                try {
                    throw new AccessDeniedException("无权访问此课程资源");
                } catch (AccessDeniedException e) {
                    throw new RuntimeException(e);
                }
            }
            courseIds = Collections.singletonList(courseId);
        }

        // 3. 查询资源
        return resourceRepository.findByCourseIdsAndTypeAndKeyword(
                courseIds, type, keyword
        );
    }

    public List<Resource> getAllTeacherResources(
            Integer teacherId,
            Resource.ResourceType type,
            String keyword) {

        // 构建查询条件
        Specification<Resource> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 教师ID过滤 - 通过课程关联教师
            predicates.add(cb.equal(
                    root.get("course").get("teacher").get("id"),
                    teacherId
            ));

            // 可选资源类型过滤
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            // 可选关键词搜索
            if (keyword != null && !keyword.isEmpty()) {
                String likePattern = "%" + keyword + "%";
                predicates.add(cb.or(
                        cb.like(root.get("name"), likePattern),
                        cb.like(root.get("description"), likePattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 添加排序（按创建时间倒序）
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        return resourceRepository.findAll(spec, sort);
    }
}