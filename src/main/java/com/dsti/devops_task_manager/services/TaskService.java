package com.dsti.devops_task_manager.services;


import com.dsti.devops_task_manager.dtos.TaskDto;
import com.dsti.devops_task_manager.entities.TaskEntity;
import com.dsti.devops_task_manager.exceptions.TaskCreationException;
import com.dsti.devops_task_manager.models.Task;
import com.dsti.devops_task_manager.repositories.TaskRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TaskService {

    final TaskRepository taskRepository;


    public TaskEntity toTaskEntity(Task task) {
        if (task == null) {
            log.warn("toTaskEntity() called with null task");
            return null;
        }

        log.debug("Converting Task to TaskEntity: {}", task);

        return TaskEntity.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .build();
    }

    public Task toTask(TaskEntity taskEntity) {
        if (taskEntity == null) {
            log.warn("toTask() called with null taskEntity");
            return null;
        }

        log.debug("Converting TaskEntity to Task: {}", taskEntity);

        return Task.builder()
                .id(taskEntity.getId())
                .title(taskEntity.getTitle())
                .description(taskEntity.getDescription())
                .status(taskEntity.getStatus())
                .build();
    }

    public Task createTask(@NonNull Task task) {
        log.info("Creating task with title: {}", task.getTitle());

        TaskEntity taskEntity = toTaskEntity(task);
        try {
            TaskEntity savedEntity = this.taskRepository.save(taskEntity);
            log.debug("Task created with ID: {}", savedEntity.getId());

            return toTask(savedEntity);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new TaskCreationException(ex.getMessage(), ex);
        }
    }


    public Task getTaskById(Long id) {
        log.info("Fetching task by ID: {}", id);

        TaskEntity taskEntity = this.taskRepository.findById(id).orElse(null);

        if (taskEntity == null) {
            log.warn("Task not found with ID: {}", id);
        } else {
            log.debug("Task retrieved: {}", taskEntity);
        }

        return toTask(taskEntity);
    }

    public void deleteTask(@NonNull Task task) {
        log.info("Deleting task with ID: {}", task.getId());

        this.taskRepository.deleteById(task.getId());

        log.debug("Task deleted: {}", task);
    }

    public Task updateTask(@NonNull Task task) {
        log.info("Updating task with ID: {}", task.getId());

        TaskEntity taskEntity = toTaskEntity(task);
        TaskEntity updatedEntity = this.taskRepository.save(taskEntity);

        log.debug("Task updated: {}", updatedEntity);

        return toTask(updatedEntity);
    }

    public List<Task> getAllTasks() {
        log.info("Fetching all tasks");

        List<TaskEntity> taskEntities = this.taskRepository.findAll();

        log.debug("Total tasks retrieved: {}", taskEntities.size());

        return taskEntities.stream()
                .map(this::toTask)
                .toList();
    }

    public Task toTask(TaskDto taskDto) {
        if (taskDto == null) {
            log.warn("toTask() called with null taskDto");
            return null;
        }

        log.debug("Converting TaskDto to Task: {}", taskDto);

        return Task.builder()
                .id(taskDto.getId())
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .status(taskDto.getStatus())
                .build();
    }


    public TaskDto toTaskDto(Task task) {
        if (task == null) {
            log.warn("toTaskDto() called with null task");
            return null;
        }


        log.debug("Converting Task to TaskDto: {}", task);

        return TaskDto
                .builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .build();
    }


}
