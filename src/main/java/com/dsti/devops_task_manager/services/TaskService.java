package com.dsti.devops_task_manager.services;


import com.dsti.devops_task_manager.entities.TaskEntity;
import com.dsti.devops_task_manager.models.Task;
import com.dsti.devops_task_manager.repositories.TaskRepository;
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


}
