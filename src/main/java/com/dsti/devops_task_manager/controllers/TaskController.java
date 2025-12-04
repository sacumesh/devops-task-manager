package com.dsti.devops_task_manager.controllers;

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

    @GetMapping("/health")
    public ResponseEntity<@NonNull Health> healthCheck() {
        return ResponseEntity.ok(Health.status(Status.UP).build());
    }

}
