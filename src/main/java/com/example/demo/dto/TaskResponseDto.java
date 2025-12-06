package com.example.demo.dto;

import com.example.demo.entity.TaskPriority;
import com.example.demo.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTOResponse {
  private Long id;
  private Long userId;
  private String title;
  private String description;
  private TaskStatus status;
  private TaskPriority priority;
  private LocalDate startDate;
  private LocalDate endDate;
  private Double plannedHours;
  private Double actualHours;
  private String taskGroupName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
