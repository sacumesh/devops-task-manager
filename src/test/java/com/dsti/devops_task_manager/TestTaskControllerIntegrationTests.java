package com.dsti.devops_task_manager;

import com.dsti.devops_task_manager.entities.TaskEntity;
import com.dsti.devops_task_manager.enums.TaskStatus;
import com.dsti.devops_task_manager.repositories.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TestTaskControllerIntegrationTests {

    private final String api = "/api/tasks";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void testHealthCheck() throws Exception {
        this.mockMvc.perform(get(this.api + "/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }


    @Test
    void testCreateTask() throws Exception {

        mockMvc.perform(post(this.api)
                        .contentType("application/json")
                        .content("""
                                {
                                    "title": "Test Task",
                                    "description": "This is a test"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("This is a test"))
                .andExpect(jsonPath("$.status").value(TaskStatus.TODO.toString()));
    }


    @Test
    void testUpdateTask() throws Exception {

        // Given: an existing task in the DB
        TaskEntity task = TaskEntity.builder()
                .title("Old Title")
                .description("Old description")
                .status(TaskStatus.TODO)
                .build();

        TaskEntity saved = this.taskRepository.save(task);

        // When + Then
        mockMvc.perform(put(this.api)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                    "id": %d,
                                    "title": "Updated Task",
                                    "description": "Updated description",
                                    "status": "COMPLETED"
                                }
                                """.formatted(saved.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testUpdateTaskWithNonExistingTask() throws Exception {

        // Given a Task that doesnt exist
        Long taskId = 1L;
        this.taskRepository.deleteById(taskId);


        // When + Then
        mockMvc.perform(put(this.api)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                    "id": %d,
                                    "title": "Updated Task",
                                    "description": "Updated description",
                                    "status": "COMPLETED"
                                }
                                """.formatted(taskId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Task not found with ID: %d".formatted(taskId)))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }


    @Test
    void testDeleteTask() throws Exception {
        // Given: an existing task in the DB
        TaskEntity task = TaskEntity.builder()
                .title("Old Title")
                .description("Old description")
                .status(TaskStatus.TODO)
                .build();

        TaskEntity entity = this.taskRepository.save(task);

        mockMvc.perform(delete(api + "/" + entity.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteTaskNonExistingTask() throws Exception {

        // Given a task id that doesn't exist
        Long taskId = 1L;
        this.taskRepository.deleteById(taskId);

        // When + Then
        mockMvc.perform(delete(api + "/" + taskId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Task not found with ID: " + taskId))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }


    @Test
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

        this.taskRepository.deleteAll();
        taskRepository.save(task1);
        taskRepository.save(task2);

        // When + Then: perform GET request and check JSON response
        mockMvc.perform(get(this.api)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[0].description").value("First task"))
                .andExpect(jsonPath("$[0].status").value("TODO"))
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$[1].description").value("Second task"))
                .andExpect(jsonPath("$[1].status").value("COMPLETED"));
    }


}
