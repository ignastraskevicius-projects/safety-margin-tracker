package org.ignast.stockinvesting.estimates.dbmigration;

import lombok.val;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.estimates.dbmigration.AppDbContainer.getDataSourceTo;

class FlywayIdempotentMigration {
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    FlywayIdempotentMigration(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    void migrateTwice(MysqlMigration migration) {
        execute(migration);
        removeMigrationMetadata();
        execute(migration);
    }

    private void removeMigrationMetadata() {
        val flywayMetadataTable = "flyway_schema_history";
        try {
            MysqlAssert.assertThat(jdbcTemplate).containsTable(flywayMetadataTable);
            jdbcTemplate.execute(format("DROP TABLE %s;", flywayMetadataTable));
        } catch (AssertionError e) {
            throw new IllegalStateException(format("Migration idempotency check failed: Supplied migration has not created expected migration metadata table '%s'", flywayMetadataTable));
        }
    }

    private void execute(MysqlMigration migration) {
        migration.migrate(dataSource);
    }
}

interface MysqlMigration {
    void migrate(DataSource dataSource);
}

class FlywayMigration implements MysqlMigration {

    public void migrate(DataSource dataSource) {
        Flyway.configure().baselineOnMigrate(true).dataSource(dataSource).load().migrate();
    }
}

@Testcontainers
class FlywayIdempotentMigrationTest {

    @Container
    private static MySQLContainer mysql = AppDbContainer.singleton();

    private static final String FLYWAY_METADATA_TABLE = "flyway_schema_history";

    private JdbcTemplate db;
    private FlywayIdempotentMigration idempotentMigration;

    @BeforeEach
    public void setup() {
        db = new JdbcTemplate(getDataSourceTo(mysql));
        db.execute(format("DROP TABLE IF EXISTS %s;", FLYWAY_METADATA_TABLE));

        Flyway.configure().dataSource(getDataSourceTo(mysql)).target(ProductionDatabaseMigrationVersion.version);

        idempotentMigration = new FlywayIdempotentMigration(getDataSourceTo(mysql));
    }

    @Test
    public void migrationShouldCreateExpectedMetadata() {
        idempotentMigration.migrateTwice(new FlywayMigration());

        MysqlAssert.assertThat(db).containsTable(FLYWAY_METADATA_TABLE);
        FlywayAssert.assertThat(db).hasJustMigrated("3");
    }

    @Test
    public void migrationShouldFailIfSuppliedMigrationDoesNotCreateExpectedMigrationMetadata() {
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> idempotentMigration.migrateTwice(new NoOpMysqlMigration())).withMessage(format("Migration idempotency check failed: Supplied migration has not created expected migration metadata table '%s'", FLYWAY_METADATA_TABLE));
    }

    class NoOpMysqlMigration implements MysqlMigration {

        @Override
        public void migrate(DataSource dataSource) {

        }
    }
}

