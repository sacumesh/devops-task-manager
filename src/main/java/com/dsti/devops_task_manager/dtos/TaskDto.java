package com.dsti.devops_task_manager.dtos;


import com.dsti.devops_task_manager.enums.TaskStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    Long id;
    String title;
    String description;
    TaskStatus status;
}
