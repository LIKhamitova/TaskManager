package com.example.demo.repository;


import com.example.demo.dto.StatisticDto;
import com.example.demo.entity.UserEntity;
import org.springframework.data.domain.Example;
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
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"roles"})
    @Query("SELECT u FROM UserEntity u")
    Page<UserEntity> findAllUsers(Pageable pageable);

//    @Query("SELECT u FROM UserEntity u " +
//            "LEFT JOIN FETCH u.roles " +
//            "WHERE u.id = :id")
//    List<UserEntity> findByIdAllWithRoles(
//            @Param("id") Long id
//    );

    @Query("SELECT " +
            "COUNT(u) " +
            "FROM UserEntity u"
    )
    Long allUserAdmin();

}