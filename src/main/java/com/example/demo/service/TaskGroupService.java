package com.example.demo.service;

import com.example.demo.dto.TaskResponseDto;
import com.example.demo.dto.TaskGroupRequestDto;
import com.example.demo.dto.TaskGroupResponseDto;
import com.example.demo.entity.TaskEntity;
import com.example.demo.entity.TaskGroupEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.TaskGroupStatus;
import com.example.demo.enums.TaskStatus;
import com.example.demo.exception.TaskNotCurrentUserException;
import com.example.demo.mapping.TaskGroupMapper;
import com.example.demo.mapping.TaskMapper;
import com.example.demo.repository.TaskGroupRepository;
import com.example.demo.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskGroupService {
    private final TaskGroupRepository taskGroupRepository;
    private final ServiceUtils serviceUtils;
    private final TaskRepository taskRepository;
    private final TaskGroupMapper mapper;
    private final TaskMapper taskMapper;

    /*
    Создание новой группы:
    -Проверка наличия задач и их владельца.
    -Группа без задач просто создается
    -Сохраняем группу у БД, чтобы получить ID
    -Если есть задачи, проверяем статус и владельца
    -Проверяем только задачи без группы
    -Добавляем в группу список задач
    -Добавляем в каждую задачу из списка группу
    -Все группы создаются со статусом ACTIVE
     */
    @Transactional
    public TaskGroupResponseDto createTaskGroup(
            TaskGroupRequestDto taskGroupRequestDto
    )
    {
        UserEntity userEntity= serviceUtils.checkAuthUser();
        log.info("Start creating group for user: {}, title: {}",  userEntity.getEmail(), taskGroupRequestDto.getName());
        TaskGroupEntity taskGroupToSave = mapper.maptoTaskGroupEntity(taskGroupRequestDto);
        taskGroupToSave.setUserEntity(userEntity);
        taskGroupToSave.setStatus(TaskGroupStatus.ACTIVE);
        TaskGroupEntity savedTaskGroup = taskGroupRepository.save(taskGroupToSave);
        List<Long> taskListId = taskGroupRequestDto.getTaskListId();
        if( taskListId !=null && !taskListId.isEmpty() ) //Есть задачи для создания
        {
            List<Long> uniqueTaskIds = taskListId.stream().distinct().toList();
            List<TaskEntity> taskEntityList = checkOwnerStatusTaskByIdAndUser(uniqueTaskIds,userEntity);
            if (!taskEntityList.isEmpty()) {
                savedTaskGroup.setTaskEntities(taskEntityList); //Добавляем задачи в группу
                taskEntityList.forEach(task -> {
                            task.setTaskGroupEntity(savedTaskGroup);}
                                      );
            }

            log.info("Added {} tasks to group id: {}", taskEntityList.size(), savedTaskGroup.getId());
        }

        log.info("Task group with id {} is created", savedTaskGroup.getId() );
        return mapper.mapToTaskGroupResponseDto(savedTaskGroup);
    }

    /*Удаление группы по Id
    -Проверка есть ли в группе задачи.
    -Удаляем группу из задач.
    -Устанавливаем группе статус DELETED

    */
    @Transactional
    public void deleteTaskGroupId(Long groupId)
    {
        UserEntity userEntity = serviceUtils.checkAuthUser();
        log.info("Start deleting group id: {} for user: {}", groupId, userEntity.getEmail());
        TaskGroupEntity taskGroup = taskGroupRepository.findActiveByIdAndUser(groupId, userEntity)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Task group not found with id: %d", groupId)));

        if (!taskGroup.getTaskEntities().isEmpty())  //Есть ли задачи
        {
            for (TaskEntity task : taskGroup.getTaskEntities())
            {
                task.setTaskGroupEntity(null);
            }
        }
        taskGroup.setStatus(TaskGroupStatus.DELETED);
        log.info("Task group deleted. ID: {}, Name: '{}', User ID: {}",
                groupId, taskGroup.getName(), userEntity.getId());
    }

    /* Обновление группы по Id
    Проверка что группа по id существует, принадлежит текущему юзеру и не удалена
    Смотрим есть ли список задач
    Заменяем старый список новым
    */

    @Transactional
    public TaskGroupResponseDto updateTaskGroup(
            Long groupId,
            TaskGroupRequestDto taskGroupRequestDto) {

        UserEntity userEntity = serviceUtils.checkAuthUser();
        log.info("Start updating group id: {} for user: {}", groupId, userEntity.getEmail());
        TaskGroupEntity taskGroupToSave = taskGroupRepository.findActiveByIdAndUser(groupId, userEntity)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Task group not found with id: %d", groupId)));


        taskGroupToSave.setName(taskGroupRequestDto.getName());
        taskGroupToSave.setDescription(taskGroupRequestDto.getDescription());

        List<Long> newTaskListId = taskGroupRequestDto.getTaskListId();

        if (newTaskListId != null) {
            updateTaskListInGroup(taskGroupToSave, newTaskListId, userEntity);
        }

        TaskGroupEntity updatedTaskGroup = taskGroupRepository.save(taskGroupToSave);
        log.info("Task group updated with id: {}", groupId);
        return mapper.mapToTaskGroupResponseDto(updatedTaskGroup);
    }

    /*Получить группу по Id*/
    public TaskGroupResponseDto getTaskGroupById(Long groupId) {
        UserEntity userEntity = serviceUtils.checkAuthUser();
        TaskGroupEntity taskGroup = taskGroupRepository
                .findActiveByIdAndUserWithTasks(groupId, userEntity)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Task group not found with id: %d", groupId)));

        log.info("Task group retrieved. ID: {}, Name: '{}', User ID: {}",
                groupId, taskGroup.getName(), userEntity.getId());

        return mapper.mapToTaskGroupResponseDto(taskGroup);
    }


    /* Получение списка задач по Id группы
    */

    public List<TaskResponseDto> getTasksInGroup(Long groupId) {

        UserEntity userEntity = serviceUtils.checkAuthUser();
        TaskGroupEntity taskGroup = taskGroupRepository.findActiveByIdAndUserWithTasks(groupId, userEntity)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Task group not found with id: %d", groupId)));

        List<TaskEntity> tasks = taskGroup.getTaskEntities();

        if (tasks == null || tasks.isEmpty()) {
            log.info("No tasks found in group id: {}", groupId);
            return Collections.emptyList();
        }

        log.info("Found {} tasks in group id: {}", tasks.size(), groupId);

        return tasks.stream()
                .map(taskMapper::mapToTaskResponseDto)
                .collect(Collectors.toList());
    }

    public List<TaskGroupResponseDto> getAllGroupsAdmin() {

        List<TaskGroupEntity> taskGroupEntityList =
                taskGroupRepository.findAllGroupsAdmin( );

        if (taskGroupEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        return taskGroupEntityList.stream()
                .map(mapper::mapToTaskGroupResponseDto)
                .collect(Collectors.toList());
    }


    public List<TaskGroupResponseDto> getAllActiveTaskGroups() {
        UserEntity userEntity = serviceUtils.checkAuthUser();
        List<TaskGroupEntity> taskGroupEntityList =
                taskGroupRepository.findActiveByUserWithTasks(userEntity);

        if (taskGroupEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        return taskGroupEntityList.stream()
                .map(mapper::mapToTaskGroupResponseDto)
                .collect(Collectors.toList());
    }

    private List<TaskEntity> checkOwnerStatusTaskByIdAndUser(
            List<Long> taskIds,
            UserEntity currentUser)

    {   List<TaskEntity> taskEntityList = new ArrayList<>();
        for (Long taskId : taskIds) {
            TaskEntity taskEntity = getTaskEntityByIdUserId( taskId, currentUser.getId());
            if(taskEntity.getTaskGroupEntity() == null) {
                taskEntityList.add(taskEntity);
            } else {
                log.debug("Task id {} is already other group", taskEntity.getId());
            }
        }
        return taskEntityList;
    }

    private void updateTaskListInGroup(
            TaskGroupEntity taskGroup,
            List<Long> newTaskList,
            UserEntity userEntity
    ) {
        List<Long> uniqueTaskList = newTaskList.stream().distinct().toList();
        List<TaskEntity> newTasks = checkOwnerStatusTaskByIdAndUser( uniqueTaskList, userEntity);

        List<Long> currentIds = taskGroup.getTaskEntities().stream()  //Если новый и старый список задач равен. Ничего не делаем
                .map(TaskEntity::getId)
                .sorted()
                .toList();

        List<Long> newIds = newTasks.stream()
                .map(TaskEntity::getId)
                .sorted()
                .toList();

        if (currentIds.equals(newIds)) {
            log.debug("Task lists are identical, skipping update");
            return;
        }

        if (!taskGroup.getTaskEntities().isEmpty()) { //Если список задач в старой группе не пустой, то удаляем его
            for (TaskEntity oldTask : taskGroup.getTaskEntities()) {
                oldTask.setTaskGroupEntity(null);
            }
            taskGroup.getTaskEntities().clear();
        }

        if (!newTasks.isEmpty()) { //Добавляем в группу новые задачи
            taskGroup.setTaskEntities(newTasks);
            for (TaskEntity task : newTasks) {
                task.setTaskGroupEntity(taskGroup);
            }
        }

        log.info("Updated tasks in group id: {}. New tasks count: {}",
                taskGroup.getId(), newTasks.size());
    }

    private TaskEntity getTaskEntityByIdUserId(Long taskId, Long userId) {
        TaskEntity taskEntity = taskRepository.findByIdAndStatusNot(taskId, TaskStatus.DELETED).
                orElseThrow(() -> new IllegalArgumentException(String.format("Not found task with id %d", taskId)));

        if (!taskEntity.getUserEntity().getId().equals(userId)) {
            throw new TaskNotCurrentUserException("Task is other user");
        }
        return taskEntity;
    }
}



