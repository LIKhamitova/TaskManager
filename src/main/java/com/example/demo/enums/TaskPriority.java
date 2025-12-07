package com.example.demo.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TaskPriority {
    @Schema(description = "Task is low priority", example = "LOW")
    LOW("Низкий"),
    @Schema(description = "Task is medium priority", example = "MEDIUM")
   MEDIUM("Средний"),
    @Schema(description = "Task is high priority", example = "HIGH")
   HIGH("Высокий");

   private final String name;
}