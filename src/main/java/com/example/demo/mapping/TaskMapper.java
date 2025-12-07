package com.example.demo.mapping;

import com.example.demo.dto.TaskRequestDto;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.entity.*;
import com.example.demo.enums.TaskPriority;
import com.example.demo.enums.TaskStatus;
import com.example.demo.enums.TaskStatusRequest;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    //из entity в dto
    public TaskResponseDto mapToTaskResponseDto(TaskEntity taskEntity) {
            TaskResponseDto dto = new TaskResponseDto();
            dto.setId(taskEntity.getId());
            dto.setTitle(taskEntity.getTitle());
            dto.setDescription(taskEntity.getDescription());
            try {
                dto.setStatus(TaskStatusRequest.valueOf(
                        taskEntity.getStatus().name()
                ));
            } catch (IllegalArgumentException e) {
                        dto.setStatus(null);
            }
            if (taskEntity.getTaskGroupEntity() != null) {
               dto.setTaskGroupName(taskEntity.getTaskGroupEntity().getName());
            } else {
                dto.setTaskGroupName(null);
            }
            dto.setPriority(taskEntity.getPriority());
            dto.setStartDate(taskEntity.getStartDate());
            dto.setEndDate(taskEntity.getEndDate());
            if (taskEntity.getUserEntity() != null) {
               dto.setUserId(taskEntity.getUserEntity().getId());
            }
            dto.setCreatedAt(taskEntity.getCreatedAt());
            dto.setUpdatedAt(taskEntity.getUpdatedAt());
            return dto;
        }

    //из dto в entity
    public TaskEntity maptoTaskEntity(TaskRequestDto taskRequestDto) {
        TaskEntity entity = new TaskEntity();
        entity.setTitle(taskRequestDto.getTitle());
        entity.setDescription(taskRequestDto.getDescription());
        if (taskRequestDto.getStatus() != null){
                try {

                    entity.setStatus(TaskStatus.valueOf(taskRequestDto.getStatus().name()));
                } catch (IllegalArgumentException e) {
                    entity.setStatus(TaskStatus.PLANNED);
                }
            } else {
                entity.setStatus(TaskStatus.PLANNED);
            }
        if(taskRequestDto.getPriority() != null) {
            entity.setPriority(taskRequestDto.getPriority());
        } else {
            entity.setPriority(TaskPriority.MEDIUM);
        }

        entity.setStartDate(taskRequestDto.getStartDate());
        entity.setEndDate(taskRequestDto.getEndDate());
        return entity;
    }

    public void updateTaskEntityFromDto(TaskEntity entity, TaskRequestDto dto) {
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            String statusName = dto.getStatus().name();

            if ("DELETED".equals(statusName)) {
                throw new IllegalArgumentException("Invalid status value. DELETED cannot be set manually.");
            }

            try {
                entity.setStatus(TaskStatus.valueOf(statusName));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(String.format("Invalid status value: " + statusName));
            }
        }
        if (dto.getPriority() != null) entity.setPriority(dto.getPriority());
        if (dto.getStartDate() != null) entity.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) entity.setEndDate(dto.getEndDate());
    }
}




