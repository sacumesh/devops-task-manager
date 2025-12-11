package com.dsti.devops_task_manager.mappers;

import com.dsti.devops_task_manager.dtos.TaskDto;
import com.dsti.devops_task_manager.entities.TaskEntity;
import com.dsti.devops_task_manager.models.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskEntity toEntity(Task task) {
        if (task == null) throw new IllegalArgumentException("task must not be null");
        return TaskEntity.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .build();
    }

    public Task toModel(TaskEntity entity) {
        if (entity == null) throw new IllegalArgumentException("entity must not be null");
        return Task.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .build();
    }

    public Task toModel(TaskDto dto) {
        if (dto == null) throw new IllegalArgumentException("dto must not be null");
        return Task.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .build();
    }

    public TaskDto toDto(Task task) {
        if (task == null) throw new IllegalArgumentException("task must not be null");
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .build();
    }
}
