package com.dsti.devops_task_manager;

import com.dsti.devops_task_manager.entities.TaskEntity;
import com.dsti.devops_task_manager.enums.TaskStatus;
import com.dsti.devops_task_manager.repositories.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TaskController using MockMvc against the full Spring context.
 * - Uses @Transactional to rollback DB changes between tests for isolation.
 * - Cleans the repository before each test to ensure deterministic results.
 * - Verifies status codes, payloads, and content-types.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class TaskControllerIntegrationTests {

    private final String api = "/api/tasks";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void cleanDatabase() {
        // Ensure a clean state before each test to avoid cross-test interference
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Get task by ID should return the task when it exists")
    void testGetTask() throws Exception {
        // Given: an existing task in the DB
        TaskEntity task = TaskEntity.builder()
                .title("Sample Task")
                .description("Task description")
                .status(TaskStatus.TODO)
                .build();
        TaskEntity saved = taskRepository.save(task);

        // When + Then: perform GET request and validate response
        mockMvc.perform(get(api + "/" + saved.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("Sample Task"))
                .andExpect(jsonPath("$.description").value("Task description"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    @DisplayName("Get task by ID should return 400 when task does not exist")
    void testGetTaskWithNonExistingTask() throws Exception {
        // Given: a task ID that doesn't exist (use a high value to avoid collisions)
        long taskId = Long.MAX_VALUE;

        // When + Then: expect TaskNotFoundException handled by @RestControllerAdvice
        mockMvc.perform(get(api + "/" + taskId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Task not found with ID: " + taskId))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("Create task should persist and return 201 with created entity")
    void testCreateTask() throws Exception {
        // Given: valid create payload
        String payload = objectMapper.writeValueAsString(new CreateTaskRequest("Test Task", "This is a test"));

        // When + Then: POST to create endpoint
        mockMvc.perform(post(api)
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("This is a test"))
                .andExpect(jsonPath("$.status").value(TaskStatus.TODO.toString()));
    }

    @Test
    @DisplayName("Update task should modify fields and return updated entity")
    void testUpdateTask() throws Exception {
        // Given: an existing task in the DB
        TaskEntity task = TaskEntity.builder()
                .title("Old Title")
                .description("Old description")
                .status(TaskStatus.TODO)
                .build();
        TaskEntity saved = taskRepository.save(task);

        // Build update payload with ObjectMapper for correctness
        String payload = """
                {
                  "id": %d,
                  "title": "Updated Task",
                  "description": "Updated description",
                  "status": "COMPLETED"
                }
                """.formatted(saved.getId());

        // When + Then: PUT to update endpoint
        mockMvc.perform(put(api)
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Update non-existing task should return 400 with error payload")
    void testUpdateTaskWithNonExistingTask() throws Exception {
        // Given: a Task ID that doesn't exist
        Long taskId = Long.MAX_VALUE;

        // Build update payload
        String payload = """
                {
                  "id": %d,
                  "title": "Updated Task",
                  "description": "Updated description",
                  "status": "COMPLETED"
                }
                """.formatted(taskId);

        // When + Then: expect error response handled by @RestControllerAdvice
        mockMvc.perform(put(api)
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Task not found with ID: %d".formatted(taskId)))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("Delete existing task should return 204 No Content")
    void testDeleteTask() throws Exception {
        // Given: an existing task in the DB
        TaskEntity task = TaskEntity.builder()
                .title("Old Title")
                .description("Old description")
                .status(TaskStatus.TODO)
                .build();
        TaskEntity entity = taskRepository.save(task);

        // When + Then: DELETE should succeed
        mockMvc.perform(delete(api + "/" + entity.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete non-existing task should return 400 with error payload")
    void testDeleteTaskNonExistingTask() throws Exception {
        // Given: a task ID that doesn't exist
        long taskId = Long.MAX_VALUE;

        // When + Then: expect error response
        mockMvc.perform(delete(api + "/" + taskId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Task not found with ID: " + taskId))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("Get all tasks should return array with expected elements")
    void testGetAllTasks() throws Exception {
        // Given: save some tasks in the repository
        TaskEntity task1 = TaskEntity.builder()
                .title("Task 1")
                .description("First task")
                .status(TaskStatus.TODO)
                .build();

        TaskEntity task2 = TaskEntity.builder()
                .title("Task 2")
                .description("Second task")
                .status(TaskStatus.COMPLETED)
                .build();

        taskRepository.save(task1);
        taskRepository.save(task2);

        // When + Then: perform GET request and check JSON response
        mockMvc.perform(get(api)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[0].description").value("First task"))
                .andExpect(jsonPath("$[0].status").value("TODO"))
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$[1].description").value("Second task"))
                .andExpect(jsonPath("$[1].status").value("COMPLETED"));
    }

    // Simple DTO used only for serialization in tests
    static class CreateTaskRequest {
        public String title;
        public String description;

        public CreateTaskRequest(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }
}
