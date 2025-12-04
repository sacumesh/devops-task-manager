package com.dsti.devops_task_manager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class EntityTableIntegrationTest {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;


    @Test
    void allEntityTablesShouldExist() {
        // Test entities are properly mapped and that tables are being created in your test database.
        for (EntityType<?> entityType : entityManager.getMetamodel().getEntities()) {

            String tableName = getString(entityType);

            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?",
                    Integer.class, tableName
            );

            assertThat(count)
                    .withFailMessage("Table for entity '%s' does not exist", tableName)
                    .isNotNull()
                    .isGreaterThan(0);

        }
    }

    private static String getString(EntityType<?> entityType) {
        Table tableAnnotation = entityType.getJavaType().getAnnotation(Table.class);

        // Determine the table name to use in the database check:
        // - If the entity has a @Table annotation, use its "name" attribute (converted to uppercase for H2 compatibility)
        // - If there is no @Table annotation, fall back to using the entity class name itself (also converted to uppercase)
        return (tableAnnotation != null) ? tableAnnotation.name().toUpperCase()
                : entityType.getName().toUpperCase();
    }
}
