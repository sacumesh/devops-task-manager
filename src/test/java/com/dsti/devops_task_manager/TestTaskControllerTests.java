package com.dsti.devops_task_manager;

import com.dsti.devops_task_manager.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TestTaskControllerTests {

    private final String api = "/api/tasks";
    @Autowired
    private MockMvc mockMvc;

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
}
