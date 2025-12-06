package com.example.demo.dto;

import com.example.demo.entity.TaskPriority;
import com.example.demo.entity.TaskStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTORequest{
        @NotBlank
        @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
        String title;
        @NotBlank
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description;
        TaskStatus status;
        TaskPriority priority;
        @NotNull
        @FutureOrPresent(message = "Start date must be today or in the future")
        LocalDate startDate;
        @NotNull
        @FutureOrPresent(message = "End date must be today or in the future")
        LocalDate endDate;
        @NotNull
        @PositiveOrZero(message = "Planned hours must be positive or zero")
        Double plannedHours;
        @NotNull
        @PositiveOrZero(message = "Actual hours must be positive or zero")
        Double actualHours;
        @Min(value = 0, message = "0 - task not belong any group.If you want, you can fill id group")
        Long taskGroupId;
         }
