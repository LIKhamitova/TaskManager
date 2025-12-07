package com.example.demo.dto;

import com.example.demo.enums.TaskPriority;
import com.example.demo.enums.TaskStatusRequest;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {
        @NotBlank
        @Size(min = 1, max = 100)
        private String title;
        @Size(max = 500)
        private String description;
        private TaskStatusRequest status;
        private TaskPriority priority;
        @NotNull
        @FutureOrPresent
        private LocalDate startDate;
        @NotNull
        @FutureOrPresent
        private LocalDate endDate;
        private Long taskGroupId;
         }
