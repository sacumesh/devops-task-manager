package com.dsti.devops_task_manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.Table;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring Boot integration tests to verify:
 * - The test datasource is wired and can open a connection.
 * - All JPA entity tables exist in the test database.
 * Uses the "test" profile.
 */
@SpringBootTest
@ActiveProfiles("test")
class TestDatabaseIntegrationTests {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    private static String getTableName(EntityType<?> entityType) {
        Table tableAnnotation = entityType.getJavaType().getAnnotation(Table.class);

        // Use @Table(name=...) if present; otherwise fall back to entity name.
        // Convert to uppercase for H2/INFORMATION_SCHEMA case-insensitive matching.
        return (tableAnnotation != null && !tableAnnotation.name().isBlank())
                ? tableAnnotation.name().toUpperCase()
                : entityType.getName().toUpperCase();
    }

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

    @Test
    @DisplayName("All JPA entity tables should exist in the test database")
    void allEntityTablesShouldExist() {
        assertThat(entityManager).as("EntityManager should be injected").isNotNull();
        assertThat(jdbcTemplate).as("JdbcTemplate should be injected").isNotNull();

        // Iterate over all entities known to the JPA metamodel
        for (EntityType<?> entityType : entityManager.getMetamodel().getEntities()) {
            String tableName = getTableName(entityType);

            // INFORMATION_SCHEMA works for H2 and many RDBMS; adjust if needed for your DB
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE UPPER(TABLE_NAME) = ?",
                    Integer.class, tableName
            );

            assertThat(count)
                    .withFailMessage("Table for entity '%s' does not exist", tableName)
                    .isNotNull()
                    .isGreaterThan(0);
        }
    }
}
