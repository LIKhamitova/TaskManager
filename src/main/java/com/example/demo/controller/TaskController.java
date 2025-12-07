package com.example.demo.controller;

import com.example.demo.dto.TaskRequestDto;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.enums.TaskPriority;
import com.example.demo.enums.TaskStatus;
import com.example.demo.enums.TaskStatusRequest;
import com.example.demo.filters.TaskSearchFilter;
import com.example.demo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "USER:Tasks", description = "Block for tasks. Only for  authorized users")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {

        private final TaskService taskService;

        @PostMapping
        @Operation(summary = "Create task", description = """
                 The rules for creating a task
                  - The task name must be unique.
                    You can specify the status of a task (PLANNED, IN_PROCESS, CANCELLED). By default.
                    Created tasks have the status default PLANNED status"
                  - You can specify the priority - LOW, MEDIUM, HIGH.
                    By default, tasks have the priority - MEDIUM.
                  - You can specify a group when creating a task. If you want to create a task without a group.
                  - The title field is required and its length must not exceed 100 characters.
                  - The description field can be empty, but its length must not exceed 500 characters.
                  - You cannot create tasks for past periods
                """)
        public ResponseEntity<TaskResponseDto> createTask(
                @Valid @RequestBody TaskRequestDto taskToCreate) {
            log.info("Called createTask");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(taskService.createTask(taskToCreate));
        }


        @GetMapping
        @Operation(summary = "Get tasks ",
                   description = """
          
               Pagination Parameters:
                  - pageNumber - Page number (0-based, default: 0)
                  - pageSize - Number of items per page (max: 50, default: 20
               """ )
        public ResponseEntity<List<TaskResponseDto>> getAllTasks(

                @RequestParam(name = "priority", required = false) TaskPriority taskPriority,
                @RequestParam(name = "status", required = false)TaskStatusRequest taskStatusRequest,
                @RequestParam(name = "pageSize",required = false) Integer pageSize,
                @RequestParam(name = "pageNumber", required = false) Integer pageNumber
        ) {
            TaskStatus taskStatus = (taskStatusRequest != null)
                    ? TaskStatus.valueOf(taskStatusRequest.name())
                    : null;

            int limitedSize = (pageSize != null) ? Math.min(pageSize, 50) : 20;
            int limitedPageNumber = (pageNumber != null) ? Math.max(pageNumber, 0) : 0;

            var filter = new TaskSearchFilter(
                    taskPriority,
                    taskStatus,
                    limitedSize,
                    limitedPageNumber
            );
            return ResponseEntity.ok(taskService.searchTasksCurrentUser(filter));
        }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by id")
    public ResponseEntity<TaskResponseDto> getTaskById(
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(taskService.getCurrentTaskID(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task by ID")
    public ResponseEntity<TaskResponseDto> updateTask (
                @PathVariable("id") Long taskId,
                @Valid @RequestBody TaskRequestDto taskRequestDto) {
            log.info("Called updateTask");

            return ResponseEntity.status(HttpStatus.OK)
                    .body(taskService.editTaskById( taskId, taskRequestDto));}

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task by ID")
    public ResponseEntity<Void> deleteTask (
            @PathVariable("id") Long taskId) {
        log.info("Called deleteTask");
        taskService.deleteTaskByID(taskId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update status task ")
    public ResponseEntity<Void> updateTaskStatus(
            @PathVariable("id") Long taskId,
            @RequestParam TaskStatusRequest newStatus) {
        log.info("Called updateTaskStatus for task id: {}, new status: {}", taskId, newStatus);
        taskService.updateTaskStatus(taskId,newStatus);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/group")
    @Operation(summary = "Assign task to group or unassign",
               description = """
               - You can add a task to a group by specifying the group ID and task ID.
               - To remove a task from a group, you need to leave the group ID empty.
               
                Note: if you want to add a task to a group that is already part of another group,
                 the task will be removed from the old group and added to the new one.
               """ )
    public ResponseEntity<TaskResponseDto> updateTaskGroup(
            @PathVariable("id") Long taskId,
            @RequestParam(required = false) Long groupId) { // null = отвязать
        log.info("Called updateTaskGroup for task id: {}, group id: {}", taskId, groupId);
        return ResponseEntity.ok(taskService.updateTaskGroup(taskId, groupId));
    }
}


