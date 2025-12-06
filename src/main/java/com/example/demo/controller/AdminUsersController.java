package com.example.demo.controller;

import com.example.demo.dto.TaskDTOResponse;
import com.example.demo.dto.UserDTOResponse;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "ADMIN:Users", description = "Block accessible only with ADMIN privileges. Viewing information about existing users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminUsersController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List all users")
    public ResponseEntity<List<UserDTOResponse>> getAllUsers() {
        log.info("Called getAllUsers");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserDTOResponse> getUserById(
            @PathVariable("id") Long id
    ) {
        log.info("Called getUserById: id={}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.findUserById(id));
    }
    @GetMapping("/{id}/tasks")
    @Operation(summary = "Get all user tasks by ID")
    public ResponseEntity<List<TaskDTOResponse>> getUserTasksByID(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserTasks(id));
    }
}