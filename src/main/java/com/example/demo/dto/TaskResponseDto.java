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
public class TaskResponseDto {
  private Long id;
  private Long userId;
  private String title;
  private String description;
  private TaskStatusRequest status;
  private TaskPriority priority;
  private LocalDate startDate;
  private LocalDate endDate;
  private String taskGroupName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
