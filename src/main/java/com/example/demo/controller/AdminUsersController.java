package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.TaskEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.TaskPriority;
import com.example.demo.enums.TaskStatus;
import com.example.demo.enums.TaskStatusRequest;
import com.example.demo.filters.TaskSearchFilter;
import com.example.demo.service.AdminRolesService;
import com.example.demo.service.TaskGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@Tag(name = "ADMIN:Users", description = "Block accessible only with ADMIN privileges. Viewing information about existing users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminUsersController {

    private final AdminRolesService adminRolesService;
    private final TaskGroupService taskGroupService;

    @GetMapping("/users")
    @Operation(summary = "General statistic about users",
            description = """
                    Retrieve a paginated list of all system users.
                      Query parameters:
                       - page - page number (0-50)
                       - size - items per page
                       Default: first page (0) with 10 items per page.
                    """)
    public ResponseEntity<Page<AdminUserResponseDto>> getAllUsersAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    )
    {
        log.info("Called getAllUsersTasksGroups");
        int limitedSize = Math.min(size, 50);

        return ResponseEntity.ok(adminRolesService.getAllUsersTasksGroups(page,limitedSize));
    }


    @GetMapping("/tasks")
    @Operation(summary = "List of all created tasks",
    description = """
                    View all created tasks.
                    Filterable by status and priority.
                    Query parameters:
                       - page - page number (0-50)
                       - size - items per page
                    Default: first page (0) with 20 items per page.
                    """)
    public ResponseEntity<Page<TaskResponseAdminDto>> getAllTasksAdmin(
             @RequestParam(name = "priority", required = false) TaskPriority taskPriority,
             @RequestParam(name = "status", required = false) TaskStatusRequest taskStatusRequest,
             @RequestParam(name = "pageSize",required = false) Integer pageSize,
             @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ) {
            TaskStatus taskStatus = (taskStatusRequest != null)
                    ? TaskStatus.valueOf(taskStatusRequest.name())
                    : null;

        int limitedSize = (pageSize != null)
                ? Math.min(pageSize, 50)
                : 20;
        int limitedPageNumber = (pageNumber != null) ? Math.max(pageNumber, 0) : 0;

            var filter = new TaskSearchFilter(
                    taskPriority,
                    taskStatus,
                    limitedSize,
                    limitedPageNumber
            );
            log.info("List of all tasks is created");
            return ResponseEntity.ok(adminRolesService.getAllGroupsAndTasks(filter));
        }

    @GetMapping("/tasks/users/{id}")
    @Operation(summary = "List of created tasks by user",
            description = """
                    Personal user statistics by tasks
                    """)

    public ResponseEntity<List<TaskResponseAdminDto>> getAllTasksByIdAdmin(
            @PathVariable("id") Long id
    ) {
        log.info("List of created tasks by user id {}",id);
        List<TaskResponseAdminDto> tasks = adminRolesService.getAllTasksByIdAdmin(id);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/groups")
    @Operation(summary = "List of all created groups",
            description = """
                    View all created groups with tasks.
                    No pages
                    """)
    public ResponseEntity<List<TaskGroupResponseDto>> getAllGroupsAdmin() {
        log.info("List of all groups is created");
        return ResponseEntity.ok(taskGroupService.getAllGroupsAdmin());
    }

    @GetMapping("/statistics")
    @Operation(summary = "All statistic",
            description = """
                    Getting overall statistics about activity in the system.
                    """)
    public ResponseEntity<StatisticDto> getAllStatistic() {
        log.info("Getting overall statistics about activity in the system");
        return ResponseEntity.ok(adminRolesService.getAllStatisticAdmin());
    }

}