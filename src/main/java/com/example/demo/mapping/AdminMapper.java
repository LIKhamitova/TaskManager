package com.example.demo.mapping;

import com.example.demo.dto.AdminUserResponseDto;
import com.example.demo.dto.TaskResponseAdminDto;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.TaskEntity;
import com.example.demo.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminMapper {
    public AdminUserResponseDto mapToAdminUserDto(UserEntity userEntity) {
        AdminUserResponseDto dto = new AdminUserResponseDto();
        dto.setId(userEntity.getId());
        dto.setName(userEntity.getName());
        dto.setEmail(userEntity.getEmail());
        dto.setUserStatus(userEntity.getUserStatus());
        dto.setCreatedAt(userEntity.getCreatedAt());
        dto.setUpdatedAt(userEntity.getUpdatedAt());

        if (userEntity.getRoles() != null) {
            List<String> roleNames = userEntity.getRoles().stream()
                    .map(RoleEntity::getName)
                    .collect(Collectors.toList());
            dto.setRoles(roleNames);
        }

        return dto;
    }

    public Page<AdminUserResponseDto> mapToAdminPageDto(Page<UserEntity> userPage) {
        return userPage.map(this::mapToAdminUserDto);
    }

    public Page<TaskResponseAdminDto> mapToTaskResponseDtoPage(Page<TaskEntity> tasks) {
        return tasks.map(this::mapToTaskResponseDto);
    }

    public List<TaskResponseAdminDto> mapToTaskResponseDtoList(List<TaskEntity> tasks) {
        return tasks.stream()
                .map(this::mapToTaskResponseDto)
                .collect(Collectors.toList());
    }

    public TaskResponseAdminDto mapToTaskResponseDto(TaskEntity task) {
        TaskResponseAdminDto dto = new TaskResponseAdminDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setPriority(task.getPriority());
        dto.setStatus(task.getStatus());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        if (task.getUserEntity() != null) {
            dto.setUserId(task.getUserEntity().getId());
            dto.setUserName(task.getUserEntity().getName());
            dto.setUserEmail(task.getUserEntity().getEmail());
        }

        if (task.getTaskGroupEntity() != null) {
            dto.setGroupId(task.getTaskGroupEntity().getId());
            dto.setGroupName(task.getTaskGroupEntity().getName());
        }
        return dto;
    }
}
