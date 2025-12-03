package com.dsti.devops_task_manager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
@ActiveProfiles("test")
public class TestDatabaseConnectionTests {
    @Autowired
    private DataSource dataSource;


    @Test
    void testConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assert connection != null;
            System.out.println("Connected to: " + connection.getMetaData().getURL());
        }
    }

}
