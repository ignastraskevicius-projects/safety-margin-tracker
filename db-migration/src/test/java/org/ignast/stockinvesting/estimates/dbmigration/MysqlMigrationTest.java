package org.ignast.stockinvesting.estimates.dbmigration;

import lombok.val;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.estimates.dbmigration.MySQLContainers.getDataSourceTo;

@Testcontainers
public class MysqlMigrationTest {

    private static final String SCHEMA_NAME = "testschema";

    @Container
    private static final MySQLContainer mysql = MySQLContainers.singleton();

    private static JdbcTemplate db;

    @BeforeAll
    public static void setup() {
        val dataSource = getDataSourceTo(mysql);
        db = new JdbcTemplate(dataSource);

        Flyway.configure().baselineOnMigrate(true).dataSource(dataSource).load().migrate();
    }

    @Test
    public void shouldCreateCompanyTable() {
        MysqlAssert.assertThat(db).containsTable("company");
    }
}

