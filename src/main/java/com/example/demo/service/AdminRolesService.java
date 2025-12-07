package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.TaskEntity;
import com.example.demo.entity.TaskGroupEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.TaskStatus;
import com.example.demo.filters.TaskSearchFilter;
//import com.example.demo.mapping.AdminMapper;
import com.example.demo.mapping.AdminMapper;
import com.example.demo.mapping.UserMapper;
import com.example.demo.repository.TaskGroupRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminRolesService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskGroupRepository taskGroupRepository;
    private final AdminMapper adminMapper;

    public Page<AdminUserResponseDto> getAllUsersTasksGroups(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> users = userRepository.findAllUsers(pageable);
        return adminMapper.mapToAdminPageDto(users);
    }
    public List<TaskResponseAdminDto> getAllTasksByIdAdmin(Long id) {
        List<TaskEntity> tasks = taskRepository.findByUserEntityId(id);
        return adminMapper.mapToTaskResponseDtoList(tasks);
    }

    public Page<TaskResponseAdminDto> getAllGroupsAndTasks(TaskSearchFilter taskSearchFilter) {

        int pageSize = taskSearchFilter.pageSize()!= null
                ? taskSearchFilter.pageSize() : 10;
        int pageNumber = taskSearchFilter.pageNumber()!= null
                ? taskSearchFilter.pageNumber() : 0;

        var pageable = Pageable.ofSize(pageSize).withPage( pageNumber);

        Page<TaskEntity> tasks = taskRepository.searchAllTaskByFilterAdmin(
                taskSearchFilter.taskPriority(),
                taskSearchFilter.taskStatus(),
                pageable
        );

        if ( tasks.isEmpty() ){
            log.info("No tasks");
       }
        return adminMapper.mapToTaskResponseDtoPage(tasks);
    }

        public StatisticDto getAllStatisticAdmin () {
            List<Object[]> taskStatsList = taskRepository.allStatTasksByAdmin();
            Long userStat = userRepository.allUserAdmin();
            Long groupStat = taskGroupRepository.allTaskGroupAdmin();
            Long totalTasks = taskRepository.countAllTasks();

            StatisticDto dto = new StatisticDto();
            dto.setTotalUsers(userStat);
            dto.setTotalTasks(totalTasks);
            dto.setTotalGroups(groupStat);

            if (taskStatsList != null && !taskStatsList.isEmpty()) {
                Object[] taskStats = taskStatsList.getFirst();
                dto.setPlannedTasks(getSafeArrayElement(taskStats, 0));
                dto.setInProcessTasks(getSafeArrayElement(taskStats, 1));
                dto.setCompletedTasks(getSafeArrayElement(taskStats, 2));
                dto.setCancelledTasks(getSafeArrayElement(taskStats, 3));
                dto.setDeletedTasks(getSafeArrayElement(taskStats, 4));
                dto.setLowPriorityTasks(getSafeArrayElement(taskStats, 5));
                dto.setMediumPriorityTasks(getSafeArrayElement(taskStats, 6));
                dto.setHighPriorityTasks(getSafeArrayElement(taskStats, 7));
            } else {
                dto.setPlannedTasks(0);
                dto.setInProcessTasks(0);
                dto.setCompletedTasks(0);
                dto.setCancelledTasks(0);
                dto.setDeletedTasks(0);
                dto.setLowPriorityTasks(0);
                dto.setMediumPriorityTasks(0);
                dto.setHighPriorityTasks(0);
            }
            log.info("System statistics prepared successfully");
            return dto;
        }

    private Object getSafeArrayElement(Object[] array, int index) {
        if (array == null || index >= array.length) {
            return 0;
        }
        return array[index] != null ? array[index] : 0;
    }
    }




