package com.example.demo.mapping;

import com.example.demo.dto.TaskGroupRequestDto;
import com.example.demo.dto.TaskGroupResponseDto;
import com.example.demo.entity.TaskEntity;
import com.example.demo.entity.TaskGroupEntity;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskGroupMapper {
    //из entity в dto
    public TaskGroupResponseDto mapToTaskGroupResponseDto(TaskGroupEntity taskGroupEntity){
           TaskGroupResponseDto dto = new TaskGroupResponseDto();
           dto.setId(taskGroupEntity.getId());
           dto.setName(taskGroupEntity.getName());
           dto.setDescription(taskGroupEntity.getDescription());
        if (taskGroupEntity.getTaskEntities() != null) {
            List<Long> taskIdList = taskGroupEntity.getTaskEntities().stream()
                    .map(TaskEntity::getId)
                    .collect(Collectors.toList());
            dto.setTaskListId(taskIdList);
        }
           dto.setCreatedAt(taskGroupEntity.getCreatedAt());
           dto.setUpdatedAt(taskGroupEntity.getUpdatedAt());
            return dto;
}
    public TaskGroupEntity maptoTaskGroupEntity(TaskGroupRequestDto taskGroupRequestDto) {
        TaskGroupEntity entity = new TaskGroupEntity();
        entity.setName(taskGroupRequestDto.getName());
        entity.setDescription(taskGroupRequestDto.getDescription());
        return entity;
    }
}
