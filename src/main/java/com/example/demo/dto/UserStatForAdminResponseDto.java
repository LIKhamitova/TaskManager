package com.example.demo.dto;

import com.example.demo.entity.RoleEntity;
import com.example.demo.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatForAdminResponseDto {
    private Long id;
    private String name;
    private String email;
    private UserStatus status;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private int tasksAllCount;
    private int tasksInStatusInProcess;
    private int tasksInStatusPlanned;
    private int tasksInStatusCompleted;
    private int tasksInStatusCancelled;
    private int tasksInStatusDeleted;
    private int taskGroupsCount;
}






