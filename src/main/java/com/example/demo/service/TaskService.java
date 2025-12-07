package com.example.demo.service;

import com.example.demo.dto.TaskRequestDto;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.entity.*;
import com.example.demo.enums.TaskPriority;
import com.example.demo.enums.TaskStatus;
import com.example.demo.enums.TaskStatusRequest;
import com.example.demo.exception.OtherOwnerException;
import com.example.demo.exception.TaskNotCurrentUserException;
import com.example.demo.filters.TaskSearchFilter;
import com.example.demo.mapping.TaskMapper;
import com.example.demo.repository.TaskGroupRepository;
import com.example.demo.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;


/* Пользователю с ролью USER доступна работа только работа со своими задачами
*/

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper mapper;
    private final ServiceUtils serviceUtils;
    private final TaskGroupRepository taskGroupRepository;

    /*Порядок создания новой задачи:
     1. Получаем данные владельца из Security Context
     2. Проверяем валидность дат
     3. Устанавливаем владельца
     4. Если указана группа, то
        - устанавливаем группу в задачу
        - сохраняем задачу в группу
        Если группа не указана, только сохраняем задачу
    */
    @Transactional
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
        UserEntity userEntity = serviceUtils.checkAuthUser();
        log.info("Start creating task for user: {}, title: {}", userEntity.getEmail(), taskRequestDto.getTitle());
        isValidDates(taskRequestDto);
        TaskEntity taskToSave = mapper.maptoTaskEntity(taskRequestDto);
        taskToSave.setUserEntity(userEntity);
        TaskEntity taskEntityNew;
        if (taskRequestDto.getTaskGroupId() != null) {
            TaskGroupEntity taskGroupEntity = setTaskGroupByIdAndUserId(taskRequestDto.getTaskGroupId(), userEntity.getId());
            taskToSave.setTaskGroupEntity(taskGroupEntity);
            taskEntityNew = taskRepository.save(taskToSave); //Сохранили задачу в БД, чтобы получить id

              log.info("Task added to group '{}' (ID: {})", taskGroupEntity.getName(), taskGroupEntity.getId());
              taskGroupEntity.getTaskEntities().add(taskEntityNew); //Сохраняю задачу в группу

        } else {
            taskEntityNew = taskRepository.save(taskToSave); //Задача без группы, просто сохраняем
            log.debug("Creating task without group assignment");
        }
            log.info("Task created successfully" );
            return mapper.mapToTaskResponseDto(taskEntityNew);
    }

    /* Изменение задачи по ID
    * 1 Получаем данные владельца
    * 2 Проверка дат на валидность
    * 3 Получение данных о задаче по id из БД с проверкой владельца по id
    * 4 Получение из запроса Id группы
    * 5 Если указан номер группы. Получаем текущую группу. Если не совпадает, то удаляем из старой и добавляем в новую
    *   Если null - удаляем группу у задачи и задачу из группы
    */

    @Transactional
    public TaskResponseDto editTaskById(Long taskId, TaskRequestDto taskRequestDto) {
        UserEntity userEntity = serviceUtils.checkAuthUser( );
        isValidDates(taskRequestDto);
        TaskEntity taskEntity = getTaskEntityByIdUserId(taskId, userEntity.getId());
        log.info("Start editing task user: {}, task id: {}", userEntity.getEmail(), taskId );
        mapper.updateTaskEntityFromDto( taskEntity, taskRequestDto);
        Long newGroupId = taskRequestDto.getTaskGroupId();
        TaskGroupEntity currentGroup = taskEntity.getTaskGroupEntity();
            if ( newGroupId  != null) {
                  TaskGroupEntity newGroup = setTaskGroupByIdAndUserId(newGroupId, userEntity.getId());
                  if ( currentGroup == null ) {
                      // Если у текущей задачи группы нет - добавляем задачу в группу и группу в задачу
                      taskEntity.setTaskGroupEntity(newGroup);
                      newGroup.getTaskEntities().add(taskEntity);
                      log.info("Task id {} add in group id {}", taskId, newGroup.getId());
                 }else {
                    //Указана группа(если не совпадают) - добавить и/или заменить
                    if ( !currentGroup.getId().equals(newGroup.getId())) {
                        currentGroup.getTaskEntities().remove(taskEntity);
                        log.info("Remove task id: {} from group id: {} ", taskId, currentGroup.getId() );

                        taskEntity.setTaskGroupEntity(newGroup);
                        newGroup.getTaskEntities().add(taskEntity);
                        log.info("Task id {} add in group id {}", taskId, newGroup.getId());
                    }
                }
            } else{ // удалить задачу из группы
                    if (currentGroup != null) {
                        currentGroup.getTaskEntities().remove(taskEntity);
                        taskEntity.setTaskGroupEntity(null);
                        log.info("Task id {} removed from group id {}", taskId, currentGroup.getId() );
                    }
                }

        log.info("Task id {} is changed successfully", taskId);
        return mapper.mapToTaskResponseDto(taskEntity);
    }

    public List<TaskResponseDto> searchTasksCurrentUser(
             TaskSearchFilter taskSearchFilter
    ) {
        UserEntity userEntity = serviceUtils.checkAuthUser( );

        int pageSize = (taskSearchFilter.pageSize() != null && taskSearchFilter.pageSize() > 0)
                ? taskSearchFilter.pageSize() : 10;

        int pageNumber = (taskSearchFilter.pageNumber() != null && taskSearchFilter.pageNumber() >= 0)
                ? taskSearchFilter.pageNumber() : 0;

        var pageable = Pageable.ofSize(pageSize).withPage( pageNumber);

        Page<TaskEntity> tasks = taskRepository.searchAllByFilter(
                                      userEntity,
                                      taskSearchFilter.taskPriority(),
                                      taskSearchFilter.taskStatus(),
                                      pageable
        );

        if ( tasks.isEmpty() ){
            log.info("No tasks found for user: {}", userEntity.getEmail());
            return Collections.emptyList();
        }

        return tasks.stream()
                .map(mapper::mapToTaskResponseDto)
               .toList();

    }



    /* Удаление задачи
    1 Проверка что введен валидный ID
    2 Проверка задачи по id и владельцу
    3 Проверка наличия группы
    4 Если есть, то удаляем задачу у группы
    5.Чистим группу у задачи
    */
    @Transactional
    public void deleteTaskByID(Long taskId)
    {
        UserEntity userEntity = serviceUtils.checkAuthUser( );
        TaskEntity taskEntity = getTaskEntityByIdUserId(taskId, userEntity.getId());
        log.info("Start deleting task by user: {}, task id: {}", userEntity.getEmail(), taskId );
            TaskGroupEntity taskGroupEntity = taskEntity.getTaskGroupEntity();
            if (taskGroupEntity != null)
            {
                taskGroupEntity.getTaskEntities().remove(taskEntity);
                log.info("Task with id {} removed from group id {}", taskId, taskGroupEntity.getId());
            }
            taskEntity.setStatus(TaskStatus.DELETED);
            taskEntity.setTaskGroupEntity(null);
            log.info("Task with id {} is deleted", taskId);

    }
    /* Получение текущей задачи по ID
    1. Проверка владельца и наличие задачи в базе
    */
    public TaskResponseDto getCurrentTaskID (Long taskId)
    {
        UserEntity userEntity = serviceUtils.checkAuthUser( );
        TaskEntity taskEntity = getTaskEntityByIdUserId(taskId, userEntity.getId());
        log.info("Get task by id {}", taskId);
        return mapper.mapToTaskResponseDto(taskEntity);
    }

    /* Обновление задачи в группе
    1 Проверка id задачи
    2 Проверка указанной группы
    3 Получение данных группы по id и владельцу
    4 Если группа не указана, то задача будет отвязана от группы
    5 Если группа указана, то задача будет добавлена в группу
    * */

    @Transactional
    public TaskResponseDto updateTaskGroup(Long taskId, Long groupId)
    {
        UserEntity userEntity = serviceUtils.checkAuthUser();
        TaskEntity taskEntity = getTaskEntityByIdUserId(taskId, userEntity.getId());
        TaskGroupEntity taskGroupEntityOld = taskEntity.getTaskGroupEntity();
        log.info("Start updating task by user: {}, task id: {}", userEntity.getEmail(), taskId );
        if ( groupId == null)
        {  // Удаление задачи из группы
            if (taskGroupEntityOld != null ) {
                taskGroupEntityOld.getTaskEntities().remove(taskEntity);
                taskEntity.setTaskGroupEntity(null);
                log.info("Task unassigned from group. Task ID: {}, Old Group ID: {}.", taskId, taskGroupEntityOld.getId());
            }
        } //Добавить задачу в группу
        else
        {
            TaskGroupEntity taskGroupNew = setTaskGroupByIdAndUserId(groupId, userEntity.getId());
            if (taskGroupEntityOld == null)
            {
                taskEntity.setTaskGroupEntity(taskGroupNew);
                taskGroupNew.getTaskEntities().add(taskEntity);
                log.info("Task assigned to group. Task ID: {}, Group ID: {}", taskId, groupId);
            } else if (!taskGroupNew.getId().equals(taskGroupEntityOld.getId()))
            {
                taskGroupNew.getTaskEntities().add(taskEntity);
                taskGroupEntityOld.getTaskEntities().remove(taskEntity);
                log.info("Task unassigned from group. Task ID: {}, Old Group ID: {}", taskId, taskGroupEntityOld.getId());
                taskEntity.setTaskGroupEntity(taskGroupNew);
                log.info("Task assigned to group. Task ID: {}, Group ID: {}", taskId, groupId);
            } else
            {
                log.debug("Task id {} already in group id {}", taskId, taskGroupEntityOld.getId());
            }
        }
        log.info("Task with id {}  is updated", taskId);
        return mapper.mapToTaskResponseDto(taskEntity);
    }

    @Transactional
    public void updateTaskStatus(Long taskId, TaskStatusRequest newStatus)
    {
        UserEntity userEntity = serviceUtils.checkAuthUser();
        TaskEntity taskEntity = getTaskEntityByIdUserId(taskId, userEntity.getId());
        log.info("Start changing status by task id: {}", taskId );
        TaskStatus oldStatus = taskEntity.getStatus();
        try {
            TaskStatus newEntityStatus = TaskStatus.valueOf(newStatus.name());

            if (oldStatus != newEntityStatus )
            {
                taskEntity.setStatus(newEntityStatus);
                log.info("Task status updated. ID: {}, Old status: {}, New status: {}",
                        taskId, oldStatus, newEntityStatus);
            }else
            {
                log.info("Task status not changed. ID: {}, Old status: {}, New status: {}",
                        taskId, oldStatus, newEntityStatus );
            }
        }catch  (IllegalArgumentException e)
        {
            throw new IllegalArgumentException( String.format("Invalid status value: %s", newStatus));
        }
    }

    private void isValidDates(TaskRequestDto taskRequestDto)
    {
        if (taskRequestDto.getStartDate() != null && taskRequestDto.getEndDate() != null) {
            if (taskRequestDto.getEndDate().isBefore(taskRequestDto.getStartDate())) {
                throw new IllegalArgumentException("End date cannot be before start date");
            }
        }
    }

    private TaskGroupEntity setTaskGroupByIdAndUserId (Long taskGroupId, Long userId)
    {
        TaskGroupEntity taskGroupEntity = taskGroupRepository.findActiveById(taskGroupId)
                .orElseThrow(() -> new IllegalArgumentException("Task group not found"));

        if (!taskGroupEntity.getUserEntity().getId().equals(userId)) {
            throw new OtherOwnerException("Group has other owner");
        }
        return taskGroupEntity;
    }

    private TaskEntity getTaskEntityByIdUserId(Long taskId, Long userId) {
        TaskEntity taskEntity = taskRepository.findByIdAndStatusNot(taskId, TaskStatus.DELETED).
                orElseThrow(() -> new IllegalArgumentException("Not found task"));

        if (!taskEntity.getUserEntity().getId().equals(userId)) {
            throw new TaskNotCurrentUserException("Task with is other user");
        }
        return taskEntity;
    }

}



