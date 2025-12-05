package com.dsti.devops_task_manager.services;

import com.dsti.devops_task_manager.dtos.TaskDto;
import com.dsti.devops_task_manager.entities.TaskEntity;
import com.dsti.devops_task_manager.enums.TaskStatus;
import com.dsti.devops_task_manager.exceptions.TaskNotFoundException;
import com.dsti.devops_task_manager.mappers.TaskMapper;
import com.dsti.devops_task_manager.models.Task;
import com.dsti.devops_task_manager.repositories.TaskRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class TaskService {

    private static final String NOT_FOUND_FMT = "Task not found with ID: %d";

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskEntity toTaskEntity(Task task) {
        if (task == null) return null;
        log.debug("Converting Task to TaskEntity, title={}", task.getTitle());
        return taskMapper.toEntity(task);
    }

    public Task toTask(TaskEntity taskEntity) {
        if (taskEntity == null) return null;
        log.debug("Converting TaskEntity to Task, id={}", taskEntity.getId());
        return taskMapper.toModel(taskEntity);
    }

    @Transactional
    public Task createTask(@Valid Task task) {
        try {
            Objects.requireNonNull(task, "Task is required");
            Objects.requireNonNull(task.getTitle(), "Title is required");
            TaskEntity taskEntity = toTaskEntity(task);
            taskEntity.setStatus(TaskStatus.TODO);
            taskEntity.setId(null);

            TaskEntity savedEntity = taskRepository.save(taskEntity);
            log.info("Task created, id={}", savedEntity.getId());
            return toTask(savedEntity);
        } catch (RuntimeException e) {
            log.error("Error creating task, title={}", task != null ? task.getTitle() : null, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Task getTask(Long id) {
        try {
            Objects.requireNonNull(id, "Task id is required");
            TaskEntity taskEntity = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException(String.format(NOT_FOUND_FMT, id)));
            return toTask(taskEntity);
        } catch (RuntimeException e) {
            log.error("Error retrieving task, id={}", id, e);
            throw e;
        }
    }

    @Transactional
    public void deleteTask(Long taskId) {
        try {
            Objects.requireNonNull(taskId, "Task id is required");
            boolean exists = taskRepository.existsById(taskId);
            if (!exists) {
                throw new TaskNotFoundException(String.format(NOT_FOUND_FMT, taskId));
            }
            taskRepository.deleteById(taskId);
            log.info("Task deleted, id={}", taskId);
        } catch (RuntimeException e) {
            log.error("Error deleting task, id={}", taskId, e);
            throw e;
        }
    }

    @Transactional
    public Task updateTask(@Valid Task task) {
        try {
            Objects.requireNonNull(task, "Task is required");
            Long id = task.getId();
            if (id == null) throw new IllegalArgumentException("Task id is required");

            TaskEntity entity = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException(String.format(NOT_FOUND_FMT, id)));

            entity.setTitle(task.getTitle());
            entity.setDescription(task.getDescription());
            entity.setStatus(task.getStatus());

            TaskEntity updatedEntity = taskRepository.save(entity);
            log.info("Task updated, id={}", updatedEntity.getId());
            return toTask(updatedEntity);
        } catch (RuntimeException e) {
            log.error("Error updating task, id={}", task != null ? task.getId() : null, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        try {
            List<TaskEntity> taskEntities = taskRepository.findAll();
            log.debug("Total tasks retrieved: {}", taskEntities.size());
            return taskEntities.stream().map(this::toTask).toList();
        } catch (RuntimeException e) {
            log.error("Error retrieving all tasks", e);
            throw e;
        }
    }

    public Task toTask(TaskDto taskDto) {
        if (taskDto == null) return null;
        log.debug("Converting TaskDto to Task, title={}", taskDto.getTitle());
        return taskMapper.toModel(taskDto);
    }

    public TaskDto toTaskDto(Task task) {
        if (task == null) return null;
        log.debug("Converting Task to TaskDto, id={}", task.getId());
        return taskMapper.toDto(task);
    }
}
