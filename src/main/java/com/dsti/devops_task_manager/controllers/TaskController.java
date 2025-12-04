package com.dsti.devops_task_manager.controllers;

import com.dsti.devops_task_manager.dtos.ErrorDto;
import com.dsti.devops_task_manager.dtos.TaskDto;
import com.dsti.devops_task_manager.exceptions.TaskNotFoundException;
import com.dsti.devops_task_manager.models.Task;
import com.dsti.devops_task_manager.services.TaskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
@Slf4j
public class TaskController {

    final TaskService taskService;

    @GetMapping("/health")
    public ResponseEntity<@NonNull Health> healthCheck() {
        return ResponseEntity.ok(Health.status(Status.UP).build());
    }

    @PostMapping
    public ResponseEntity<@NonNull TaskDto> createTask(@RequestBody TaskDto taskDto) {
        Task task = this.taskService.toTask(taskDto);
        Task createdTask = this.taskService.createTask(task);
        TaskDto responseDto = this.taskService.toTaskDto(createdTask);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping
    public ResponseEntity<@NonNull TaskDto> updateTask(@RequestBody TaskDto taskDto) {
        Task task = this.taskService.toTask(taskDto);
        Task updateTask = this.taskService.updateTask(task);
        TaskDto responseDto = this.taskService.toTaskDto(updateTask);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<@NonNull Void> deleteTask(@PathVariable @NonNull Long id) {
        this.taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<@NonNull List<TaskDto>> getAllTasks() {
        List<TaskDto> dtos = taskService.getAllTasks().stream()
                .map(taskService::toTaskDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }


    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<@NonNull ErrorDto> handleTaskNotFound(TaskNotFoundException ex) {
        ErrorDto dto = ErrorDto
                .builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

}
