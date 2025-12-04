package com.dsti.devops_task_manager.controllers;

import com.dsti.devops_task_manager.dtos.TaskDto;
import com.dsti.devops_task_manager.models.Task;
import com.dsti.devops_task_manager.services.TaskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

}
