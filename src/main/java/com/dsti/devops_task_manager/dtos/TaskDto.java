package com.dsti.devops_task_manager.dtos;


import com.dsti.devops_task_manager.enums.TaskStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskDto {
    Long id;
    String title;
    String description;
    TaskStatus status;
}
