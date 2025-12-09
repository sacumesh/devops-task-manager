package com.dsti.devops_task_manager.controllers;

import com.dsti.devops_task_manager.dtos.ApiVersionDto;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/version", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class ApiVersionController {

    @Value("${api.version}")
    private String apiVersion;

    @GetMapping("")
    public ResponseEntity<@NonNull ApiVersionDto> healthCheck() {
        return ResponseEntity.ok(ApiVersionDto.builder().apiVersion(this.apiVersion).build());
    }

}
