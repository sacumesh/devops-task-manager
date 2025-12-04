package com.dsti.devops_task_manager.services;


import com.dsti.devops_task_manager.entities.TaskEntity;
import com.dsti.devops_task_manager.models.Task;
import com.dsti.devops_task_manager.repositories.TaskRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskService {

    final TaskRepository taskRepository;


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

    public Task createTask(@NonNull Task task) {
        TaskEntity taskEntity = toTaskEntity(task);

        TaskEntity savedEntity = this.taskRepository.save(taskEntity);

        return toTask(savedEntity);
    }


    public Task getTaskById(Long id) {
        TaskEntity taskEntity = this.taskRepository.findById(id).orElse(null);
        return toTask(taskEntity);
    }


    public void deleteTask(@NonNull Task task) {
        this.taskRepository.deleteById(task.getId());
    }


    public Task updateTask(@NonNull Task task) {
        TaskEntity taskEntity = toTaskEntity(task);

        TaskEntity updatedEntity = this.taskRepository.save(taskEntity);
        
        return toTask(updatedEntity);
    }


}
