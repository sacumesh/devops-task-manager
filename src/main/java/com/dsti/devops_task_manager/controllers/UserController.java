package com.dsti.devops_task_manager.controllers;


import org.jspecify.annotations.NonNull;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class UserController {

    @GetMapping("/health")
    public ResponseEntity<@NonNull Health> healthCheck() {
        return ResponseEntity.ok(Health.status(Status.UP).build());
    }


}
