package com.dsti.devops_task_manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring Boot integration test to verify the test datasource is wired
 * and can open a connection. Uses the "test" profile.
 */
@SpringBootTest
@ActiveProfiles("test")
class TestDatabaseConnectionTests {

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("DataSource should provide a valid connection and execute a simple query")
    void testConnection() throws SQLException {
        // Ensure the DataSource bean is injected
        assertThat(dataSource)
                .as("DataSource should be configured for the test profile")
                .isNotNull();

        // Try-with-resources to ensure the connection is closed
        try (Connection connection = dataSource.getConnection()) {
            // Basic connection sanity checks
            assertThat(connection.isValid(2))
                    .as("Connection should be valid within 2 seconds")
                    .isTrue();

            // Optional: run a lightweight validation query (works for most RDBMS)
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {
                assertThat(rs.next())
                        .as("Validation query should return at least one row")
                        .isTrue();
            }

            // Keep a simple trace of which DB URL we connected to (useful in CI)
            assertThat(connection.getMetaData().getURL())
                    .as("Connected database URL should not be blank")
                    .isNotBlank();
        }
    }
}
