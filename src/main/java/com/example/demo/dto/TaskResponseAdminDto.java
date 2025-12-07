package com.example.demo.dto;

import com.example.demo.enums.TaskPriority;
import com.example.demo.enums.TaskStatus;
import com.example.demo.enums.TaskStatusRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseAdminDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long groupId;
    private String groupName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
