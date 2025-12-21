package com.dsti.devops_task_manager.controllers;

import com.dsti.devops_task_manager.dtos.ErrorDto;
import com.dsti.devops_task_manager.dtos.TaskDto;
import com.dsti.devops_task_manager.exceptions.TaskNotFoundException;
import com.dsti.devops_task_manager.models.Task;
import com.dsti.devops_task_manager.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(path = "/api/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull TaskDto> getTask(@PathVariable @NonNull Long id) {
        log.debug("Fetching task with id={}", id);
        Task task = taskService.getTask(id);
        TaskDto responseDto = taskService.toTaskDto(task);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("")
    public ResponseEntity<@NonNull List<TaskDto>> getAllTasks() {
        log.debug("Fetching all tasks");
        List<TaskDto> dtos = taskService.getAllTasks()
                .stream()
                .map(taskService::toTaskDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<@NonNull TaskDto> createTask(@Valid @RequestBody TaskDto taskDto,
                                                       UriComponentsBuilder uriBuilder) {
        log.debug("Creating task: {}", taskDto);
        Task toCreate = taskService.toTask(taskDto);
        Task createdTask = taskService.createTask(toCreate);
        TaskDto responseDto = taskService.toTaskDto(createdTask);

        URI location = uriBuilder
                .path("/api/tasks/{id}")
                .buildAndExpand(createdTask.getId())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<@NonNull TaskDto> updateTask(@Valid @RequestBody TaskDto taskDto) {
        log.debug("Updating task: {}", taskDto);
        Task toUpdate = taskService.toTask(taskDto);
        Task updatedTask = taskService.updateTask(toUpdate);
        TaskDto responseDto = taskService.toTaskDto(updatedTask);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<@NonNull Void> deleteTask(@PathVariable @NonNull Long id) {
        log.debug("Deleting task with id={}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<@NonNull ErrorDto> handleTaskNotFound(TaskNotFoundException ex) {
        ErrorDto dto = ErrorDto.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }
}
