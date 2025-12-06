package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskGroupDTOResponse {
    private Long id;

    private String name;
    private String description;
    private List<Long> taskListId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
