package com.dsti.devops_task_manager.services;


import com.dsti.devops_task_manager.entities.TaskEntity;
import com.dsti.devops_task_manager.models.Task;
import org.springframework.stereotype.Service;

@Service
public class TaskService {


    public TaskEntity toTaskEntity(Task task) {

        if (task == null) return null;

        return TaskEntity.
                builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .build();
    }


    public Task toTask(TaskEntity taskEntity) {

        if (taskEntity == null) return null;

        return Task
                .builder()
                .id(taskEntity.getId())
                .title(taskEntity.getTitle())
                .description(taskEntity.getDescription())
                .status(taskEntity.getStatus())
                .build();
    }


}
