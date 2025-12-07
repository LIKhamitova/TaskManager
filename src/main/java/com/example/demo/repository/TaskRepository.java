package com.example.demo.repository;

import com.example.demo.entity.TaskEntity;
import com.example.demo.entity.TaskGroupEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.TaskPriority;
import com.example.demo.enums.TaskStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Optional<TaskEntity> findByIdAndStatusNot(Long Id, TaskStatus status);
    @EntityGraph(attributePaths = {"userEntity", "taskGroupEntity"})
    List<TaskEntity> findByUserEntityId(Long id);

    @Query("SELECT t FROM TaskEntity t " +
            "WHERE t.userEntity = :user " +
            "AND t.status <> 'DELETED' " +
            "AND (:priority IS NULL OR t.priority = :priority) " +
            "AND (:status IS NULL OR t.status = :status)")
    Page<TaskEntity> searchAllByFilter(
            @Param("user") UserEntity user,
            @Param("priority") TaskPriority priority,
            @Param("status") TaskStatus status,
            Pageable pageable);


    @EntityGraph(attributePaths = {"userEntity", "taskGroupEntity"})
    @Query(value = """
    SELECT t FROM TaskEntity t
    WHERE (:priority IS NULL OR t.priority = :priority)
    AND (:status IS NULL OR t.status = :status)
    ORDER BY t.createdAt DESC
    """,
            countQuery = """
    SELECT COUNT(t) FROM TaskEntity t
    WHERE (:priority IS NULL OR t.priority = :priority)
    AND (:status IS NULL OR t.status = :status)
    """)
    Page<TaskEntity> searchAllTaskByFilterAdmin(
            @Param("priority") TaskPriority priority,
            @Param("status") TaskStatus status,
            Pageable pageable);

    @Query("SELECT COUNT(t) FROM TaskEntity t")
    Long countAllTasks();

    @Query("SELECT " +
            "SUM(CASE WHEN t.status = 'PLANNED' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.status = 'IN_PROCESS' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.status = 'CANCELLED' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.status = 'DELETED' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.priority = 'LOW' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.priority = 'MEDIUM' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.priority = 'HIGH' THEN 1 ELSE 0 END) " +
            "FROM TaskEntity t")
    List<Object[]> allStatTasksByAdmin( );
}

