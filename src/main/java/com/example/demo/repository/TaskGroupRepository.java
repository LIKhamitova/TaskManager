package com.example.demo.repository;

import com.example.demo.entity.TaskGroupEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.TaskGroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskGroupRepository extends JpaRepository<TaskGroupEntity, Long> {

//    @Query("SELECT g FROM TaskGroupEntity g " +
//            "WHERE g.userEntity = :user" +
//            " AND g.status <> 'DELETED'")
//    List<TaskGroupEntity> findActiveByUser(
//            @Param("user") UserEntity userEntity);

    @Query("SELECT g FROM TaskGroupEntity g " +
            "WHERE g.id = :id " +
            "AND g.userEntity = :user " +
            "AND g.status <> 'DELETED'")
    Optional<TaskGroupEntity> findActiveByIdAndUser(
            @Param("id") Long id,
            @Param("user") UserEntity userEntity);

    @Query("SELECT g FROM TaskGroupEntity g " +
            "WHERE g.id = :id " +
            "AND g.status <> 'DELETED'")
    Optional<TaskGroupEntity> findActiveById(@Param("id") Long id);

    @Query("SELECT g FROM TaskGroupEntity g LEFT JOIN FETCH g.taskEntities " +
            "WHERE g.id = :id AND g.userEntity = :user " +
            "AND g.status <> 'DELETED'")
    Optional<TaskGroupEntity> findActiveByIdAndUserWithTasks(
            @Param("id") Long id,
            @Param("user") UserEntity userEntity);

    @Query("SELECT g FROM TaskGroupEntity g " +
            "LEFT JOIN FETCH g.taskEntities " +
            "WHERE g.userEntity = :user AND g.status <> 'DELETED' ")
    List<TaskGroupEntity> findActiveByUserWithTasks(
             @Param("user") UserEntity userEntity);

//
//    @Query("SELECT g FROM TaskGroupEntity g " +
//            "WHERE g.userEntity IN :user")
//    List<TaskGroupEntity> findAllByUsers(
//            @Param("user") List<UserEntity>userEntity
//    );

    @Query("SELECT g FROM TaskGroupEntity g " +
            "LEFT JOIN FETCH g.taskEntities "
    )
    List<TaskGroupEntity> findAllGroupsAdmin( );

    @Query("SELECT " +
            "COUNT(g) " +
            "FROM TaskGroupEntity g"
    )
    Long allTaskGroupAdmin();

}

