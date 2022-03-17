package org.ignast.stockinvesting.quotes.persistence.dbmigration;

import lombok.val;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.persistence.dbmigration.AppDbContainer.getDataSourceTo;

public final class FlywayIdempotentMigration {
    private final JdbcTemplate jdbcTemplate;

    private final DataSource dataSource;

    FlywayIdempotentMigration(final DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    void migrateTwice(final String version, final MysqlMigration migration) {
        execute(version, migration);
        removeMigrationMetadata(version);
        execute(version, migration);
    }

    private void removeMigrationMetadata(final String version) {
        final val flywayMetadataTable = "flyway_schema_history";
        try {
            MysqlAssert.assertThat(jdbcTemplate).containsTable(flywayMetadataTable);
            FlywayAssert.assertThat(jdbcTemplate).hasJustMigrated(version);
            jdbcTemplate.execute(format("DELETE FROM %s WHERE installed_rank IN (SELECT last_installed_rank FROM (SELECT MAX(installed_rank) AS last_installed_rank FROM %s) AS l);", flywayMetadataTable, flywayMetadataTable));
        } catch (AssertionError e) {
            throw new IllegalStateException(format("Migration idempotency check failed: Supplied migration has not created expected migration record for version '%s' in metadata table '%s'", version, flywayMetadataTable));
        }
    }

    private void execute(final String version, final MysqlMigration migration) {
        migration.migrate(version, dataSource);
    }
}

interface MysqlMigration {
    void migrate(String version, DataSource dataSource);
}

final class FlywayMigration implements MysqlMigration {

    public void migrate(final String version, final DataSource dataSource) {
        Flyway.configure().baselineOnMigrate(true).dataSource(dataSource).target(version).load().migrate();
    }
}

@Testcontainers
final class FlywayIdempotentMigrationTest {

    @Container
    private static final MySQLContainer MYSQL = AppDbContainer.singleton();

    private static final String FLYWAY_METADATA_TABLE = "flyway_schema_history";

    private JdbcTemplate db;

    private FlywayIdempotentMigration idempotentMigration;

    @BeforeEach
    public void setup() {
        db = new JdbcTemplate(getDataSourceTo(MYSQL));
        db.execute(format("DROP TABLE IF EXISTS %s;", FLYWAY_METADATA_TABLE));

        Flyway.configure().dataSource(getDataSourceTo(MYSQL)).baselineOnMigrate(true).target(ProductionDatabaseMigrationVersions.CURRENT);

        idempotentMigration = new FlywayIdempotentMigration(getDataSourceTo(MYSQL));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "3"})
    public void migrationShouldCreateExpectedMetadata(final String toVersion) {
        idempotentMigration.migrateTwice(toVersion, new FlywayMigration());

        FlywayAssert.assertThat(db).hasJustMigrated(toVersion);
    }

    @Test
    public void migrationShouldFailIfSuppliedMigrationDoesNotCreateExpectedMigrationMetadata() {
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> idempotentMigration.migrateTwice("2", new NoOpMysqlMigration())).withMessage(format("Migration idempotency check failed: Supplied migration has not created expected migration record for version '%s' in metadata table '%s'", "2", FLYWAY_METADATA_TABLE));
    }

    private static final class NoOpMysqlMigration implements MysqlMigration {

        @Override
        public void migrate(final String version, final DataSource dataSource) {

        }
    }
}

