package com.dsti.devops_task_manager;

import com.dsti.devops_task_manager.entities.TaskEntity;
import com.dsti.devops_task_manager.enums.TaskStatus;
import com.dsti.devops_task_manager.exceptions.TaskCreationException;
import com.dsti.devops_task_manager.models.Task;
import com.dsti.devops_task_manager.repositories.TaskRepository;
import com.dsti.devops_task_manager.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TestTaskServiceMock {

    @Autowired
    TaskService taskService;

    @MockitoBean
    TaskRepository taskRepository;

    @Test
    void testCreateTaskShouldThrowException() {
        // Given
        Task task = Task.builder()
                .title("Test Task")
                .description("This is a test")
                .status(TaskStatus.TODO)
                .build();

        // Simulate an exception when saving the task
        when(taskRepository.save(any(TaskEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        TaskCreationException exception = assertThrows(TaskCreationException.class, () -> taskService.createTask(task));

        // Verify message
        assertTrue(exception.getMessage().contains("Database error"));


    }
}
