package com.example.demo.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "Task status in request")
public enum TaskStatusRequest {
    @Schema(description = "No actions have been taken on this task yet", example = "PLANNED")
    PLANNED("Planned"),
    @Schema(description = "Task is in progress", example = "IN_PROGRESS")
    IN_PROGRESS("In Progress"),
    @Schema(description = "Task is completed", example = "COMPLETED")
    COMPLETED("Completed"),
    @Schema(description = "Task is cancelled", example = "CANCELLED")
    CANCELLED("Cancelled");

    private final String displayName;

    }
