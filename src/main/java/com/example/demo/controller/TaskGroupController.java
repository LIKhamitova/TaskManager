package com.example.demo.controller;

import com.example.demo.dto.TaskResponseDto;
import com.example.demo.dto.TaskGroupRequestDto;
import com.example.demo.dto.TaskGroupResponseDto;
import com.example.demo.service.TaskGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "USER:Groups", description = "Block for groups. Only for  authorized users")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskGroupController {
    private final TaskGroupService taskGroupService;


    @PostMapping( )
    @Operation(summary = "Create group",
            description = """
               The group name must be unique.
               The list of tasks can be specified, or the field can be left empty.
               """ )
    public ResponseEntity<TaskGroupResponseDto> createTaskGroup(
            @Valid @RequestBody TaskGroupRequestDto taskGroupRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskGroupService.createTaskGroup(taskGroupRequestDto));
    }

    @GetMapping
    @Operation(summary = "Get all groups")
    public ResponseEntity<List<TaskGroupResponseDto>> getAllTaskGroups() {
        return ResponseEntity.ok(taskGroupService.getAllActiveTaskGroups());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Show group by id")
    public ResponseEntity<TaskGroupResponseDto> getTaskGroupById(
            @PathVariable Long id) {
        return ResponseEntity.ok(taskGroupService.getTaskGroupById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update group by id")
    public ResponseEntity<TaskGroupResponseDto> updateTaskGroup(
            @PathVariable Long id,
            @Valid @RequestBody TaskGroupRequestDto updateDTO
    ) {
        return ResponseEntity.ok(taskGroupService.updateTaskGroup(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete group by id")
    public ResponseEntity<Void> deleteTaskGroup(
            @PathVariable Long id) {
        taskGroupService.deleteTaskGroupId(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/tasks")
    @Operation(summary = "Get all tasks in group")
    public ResponseEntity<List<TaskResponseDto>> getTasksInGroup(
            @PathVariable Long id) {
        return ResponseEntity.ok(taskGroupService.getTasksInGroup(id));
    }
    }







