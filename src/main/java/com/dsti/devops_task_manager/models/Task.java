package com.dsti.devops_task_manager.models;

import com.dsti.devops_task_manager.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Task {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
}
