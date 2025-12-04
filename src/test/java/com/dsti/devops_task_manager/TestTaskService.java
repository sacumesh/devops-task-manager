package com.dsti.devops_task_manager;


import com.dsti.devops_task_manager.entities.TaskEntity;
import com.dsti.devops_task_manager.enums.TaskStatus;
import com.dsti.devops_task_manager.models.Task;
import com.dsti.devops_task_manager.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TestTaskService {

    @Autowired
    TaskService taskService;

    @Test
    void testToTaskEntityWithNull() {
        // Given: a null Task
        // When: converting to TaskEntity
        TaskEntity entity = taskService.toTaskEntity(null);

        // Then: the result should be null
        assertThat(entity).isNull();
    }

    @Test
    public void testToTaskEntityWithNonnull() {
        // Given: a Task model
        Task task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("This is a test")
                .status(TaskStatus.COMPLETED)
                .build();

        // When: converting to TaskEntity
        TaskEntity entity = taskService.toTaskEntity(task);

        // Then: verify all fields are correctly mapped
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(task.getId());
        assertThat(entity.getTitle()).isEqualTo(task.getTitle());
        assertThat(entity.getDescription()).isEqualTo(task.getDescription());
        assertThat(entity.getStatus()).isEqualTo(task.getStatus());
    }


    @Test
    void testToTaskWithNull() {
        // Given: a null TaskEntity
        // When: converting to Task
        Task task = taskService.toTask(null);

        // Then: the result should be null
        assertThat(task).isNull();
    }

    @Test
    public void testToTaskWithNonnull() {
        // Given: a Task Entity
        TaskEntity entity = TaskEntity.builder()
                .id(1L)
                .title("Test Task")
                .description("This is a test")
                .status(TaskStatus.COMPLETED)
                .build();

        // When: converting to Task
        Task task = taskService.toTask(entity);

        // Then: verify all fields are correctly mapped
        assertThat(task).isNotNull();
        assertThat(task.getId()).isEqualTo(entity.getId());
        assertThat(task.getTitle()).isEqualTo(entity.getTitle());
        assertThat(task.getDescription()).isEqualTo(entity.getDescription());
        assertThat(task.getStatus()).isEqualTo(entity.getStatus());
    }
}
