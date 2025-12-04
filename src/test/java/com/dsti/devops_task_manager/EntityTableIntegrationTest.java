package com.dsti.devops_task_manager;

import jakarta.persistence.EntityManager;
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

            String tableName = entityType.getName().toUpperCase();

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
}
