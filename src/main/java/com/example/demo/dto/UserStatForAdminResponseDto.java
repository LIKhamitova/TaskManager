package com.example.demo.dto;

import com.example.demo.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatForAdminResponseDto {
    private Long id;
    private String name;
    private String email;
    private UserStatus status;
    private List<String> roles;
    private LocalDateTime createdAt;
    private long tasksAllCount;
    private long tasksInStatusInProcess;
    private long tasksInStatusActive;
    private long tasksInStatusCancelled;
    private long tasksInStatusDeleted;
    private int  taskGroupsCount;
}




}
