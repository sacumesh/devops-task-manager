package com.dsti.devops_task_manager;


import com.dsti.devops_task_manager.dtos.TaskDto;
import com.dsti.devops_task_manager.entities.TaskEntity;
import com.dsti.devops_task_manager.enums.TaskStatus;
import com.dsti.devops_task_manager.models.Task;
import com.dsti.devops_task_manager.repositories.TaskRepository;
import com.dsti.devops_task_manager.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TestTaskService {

    @Autowired
    TaskService taskService;

    @Autowired
    TaskRepository taskRepository;


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
    void testToWithTaskDtoWithNull() {
        // Given: a null TaskDto
        // When: converting to Task
        Task task = taskService.toTask((TaskDto) null); // cast null to TaskDto

        // Then: the result should be null
        assertThat(task).isNull();
    }

    @Test
    void testToTaskWithTaskEntityWithNull() {
        // Given: a null Task entity
        // When: converting to Task (DTO to entity overload)
        Task task = taskService.toTask((TaskEntity) null); // cast null to Task

        // Then: the result should be null
        assertThat(task).isNull();
    }


    @Test
    public void testToTaskWithTaskDto() {
        // Given: a Task Dto
        TaskDto dto = TaskDto.builder()
                .id(1L)
                .title("Test Task")
                .description("This is a test")
                .status(TaskStatus.COMPLETED)
                .build();

        // When: converting to Task
        Task task = taskService.toTask(dto);

        // Then: verify all fields are correctly mapped
        assertThat(task).isNotNull();
        assertThat(task.getId()).isEqualTo(dto.getId());
        assertThat(task.getTitle()).isEqualTo(dto.getTitle());
        assertThat(task.getDescription()).isEqualTo(dto.getDescription());
        assertThat(task.getStatus()).isEqualTo(dto.getStatus());
    }


    @Test
    public void testToTaskWithTaskEntity() {
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

    @Test
    public void testCreateTask() {
        // Given: a new Task model to be saved
        Task task = Task.builder()
                .title("Test Task")
                .description("This is a test")
                .status(TaskStatus.TODO)
                .build();

        // When: creating the task
        Task createdTask = taskService.createTask(task);

        // Then: verify returned task has an id and fields match
        assertThat(createdTask.getId()).isNotNull();
        assertThat(createdTask.getTitle()).isEqualTo(task.getTitle());
        assertThat(createdTask.getDescription()).isEqualTo(task.getDescription());
        assertThat(createdTask.getStatus()).isEqualTo(task.getStatus());
    }

    @Test
    public void testGetTaskByIdWithExistingTask() {
        // Given: a TaskEntity persisted in the repository
        TaskEntity taskEntity = TaskEntity.builder()
                .title("Test Task")
                .description("Test description")
                .status(TaskStatus.TODO)
                .build();

        TaskEntity savedEntity = taskRepository.save(taskEntity);

        // When: retrieving task by ID using the service
        Task task = taskService.getTaskById(savedEntity.getId());

        // Then: verify task is returned and fields match
        assertThat(task).isNotNull();
        assertThat(task.getId()).isEqualTo(savedEntity.getId());
        assertThat(task.getTitle()).isEqualTo(savedEntity.getTitle());
        assertThat(task.getDescription()).isEqualTo(savedEntity.getDescription());
        assertThat(task.getStatus()).isEqualTo(savedEntity.getStatus());
    }


    @Test
    public void testGetTaskByIdWithNonExistingTask() {
        // Given: ensure the task with ID 2L does not exist
        taskRepository.deleteById(2L);

        // When: retrieving task by ID using the service
        Task task = taskService.getTaskById(2L);

        // Then: verify task is null
        assertThat(task).isNull();
    }


    @Test
    public void deleteTask() {
        // No need to check deletion of non-existent ID:
        // Spring Data JPA deleteById is idempotent and does nothing if the entity does not exist,
        // so there is no exception or side effect to verify.

        // Given: a persisted TaskEntity
        TaskEntity taskEntity = TaskEntity.builder()
                .title("Test Task")
                .description("Test description")
                .status(TaskStatus.TODO)
                .build();

        TaskEntity savedEntity = taskRepository.save(taskEntity);

        // And a Task model representing the same entity
        Task task = Task.builder()
                .id(savedEntity.getId())
                .title(savedEntity.getTitle())
                .description(savedEntity.getDescription())
                .status(savedEntity.getStatus())
                .build();

        // When: deleting the task via service
        taskService.deleteTask(task);

        // Then: the entity should no longer exist in the repository
        boolean exists = taskRepository.existsById(savedEntity.getId());
        assertThat(exists).isFalse();

    }

    @Test
    public void testUpdateTask() {
        // Given: a TaskEntity persisted in the database
        TaskEntity entity = TaskEntity.builder()
                .title("Original Title")
                .description("Original Description")
                .status(TaskStatus.TODO)
                .build();

        TaskEntity savedEntity = taskRepository.save(entity);

        // And a Task model representing the same entity with updated fields
        Task task = Task.builder()
                .id(savedEntity.getId())
                .title("Updated Title")
                .description("Updated Description")
                .status(TaskStatus.COMPLETED)
                .build();

        // When: updating the task via the service
        Task updatedTask = taskService.updateTask(task);

        // Then: verify returned task has the updated fields
        assertThat(updatedTask).isNotNull();
        assertThat(updatedTask.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedTask.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTask.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);


    }


    @Test
    void testGetAllTasks() {
        // Given: a few TaskEntity objects persisted in the database
        TaskEntity taskEntity1 = TaskEntity.builder()
                .title("Task 1")
                .description("Description 1")
                .status(TaskStatus.TODO)
                .build();

        TaskEntity taskEntity2 = TaskEntity.builder()
                .title("Task 2")
                .description("Description 2")
                .status(TaskStatus.COMPLETED)
                .build();

        // Ensures only the new tasks exist the service using the repository
        taskRepository.deleteAll();

        TaskEntity savedTaskEntity1 = taskRepository.save(taskEntity1);
        TaskEntity savedTaskEntity2 = taskRepository.save(taskEntity2);

        // When: retrieving all tasks via the service
        List<Task> tasks = taskService.getAllTasks();

        // Then: verify the list contains all persisted tasks
        assertThat(tasks).isNotNull();
        assertThat(tasks).hasSize(2);

        assertThat(tasks).extracting(Task::getId)
                .containsExactlyInAnyOrder(savedTaskEntity1.getId(), savedTaskEntity2.getId());
    }
}
