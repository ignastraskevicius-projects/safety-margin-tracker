package org.ignast.stockinvesting.estimates.dbmigration;

import com.datastax.oss.driver.api.core.CqlSession;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CognitorLibIdempotentMigration {
    private final CassandraSessions cassandraSessions;
    private final String keyspace;

    CognitorLibIdempotentMigration(int port, String keyspace) {
        this.keyspace = keyspace;
        cassandraSessions = new CassandraSessions(port, keyspace);
    }

    void migrateEnsuringIdempotency(Migration migration) {
        execute(migration);
        removeMigrationMetadata();
        execute(migration);
    }

    private void removeMigrationMetadata() {
        try (val session = cassandraSessions.newSession()) {
            dropExistingMigrationTable(session);
            session.execute("DROP TABLE schema_migration_leader;");
        }
    }

    private void dropExistingMigrationTable(CqlSession session) {
        if (session.execute(format("SELECT table_name FROM system_schema.tables WHERE keyspace_name = '%s' AND table_name = '%s';", keyspace, "schema_migration")).all().isEmpty()) {
            throw new IllegalStateException("Migration idempotency check failed: Supplied migration has not created expected metadata");
        }
        session.execute("DROP TABLE schema_migration;");
    }

    private void execute(Migration migration) {
        try (val session = cassandraSessions.newSession()) {
            migration.migrate(session, keyspace);
        };
    }
}

@Testcontainers
class CognitorLibIdempotentMigrationTest {

    @Container
    private static GenericContainer cassandra = new CassandraContainer("cassandra:4.0.3");

    private int port;

    @BeforeEach
    public void setup() {
        port = cassandra.getMappedPort(9042);
    }

    @Test
    public void migrationShouldCreateExpectedMetadata() {
        try (val session = new CassandraSessions(port, "somekeyspace").newSession()) {
            session.execute("DROP TABLE IF EXISTS schema_migration;");
            val idempotentMigration = new CognitorLibIdempotentMigration(port, "somekeyspace");

            idempotentMigration.migrateEnsuringIdempotency(new CognitorLibCassandraMigration());

            assertThat(session.execute("DESCRIBE TABLE schema_migration;").all()).isNotEmpty();
        }
    }

    @Test
    public void migrationShouldFailIfSupliedMigratorDoesNotCreateExpectedMigrationMetadata() {
        try (val session = new CassandraSessions(port, "somekeyspace").newSession()) {
            assertThat(session.execute("DROP TABLE IF EXISTS schema_migration;").all()).isEmpty();
            val idempotentMigration = new CognitorLibIdempotentMigration(port, "somekeyspace");

            assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> idempotentMigration.migrateEnsuringIdempotency(new NoOpMigration())).withMessage("Migration idempotency check failed: Supplied migration has not created expected metadata");
        }
    }

    class NoOpMigration implements Migration {

        @Override
        public void migrate(CqlSession session, String keyspace) {

        }
    }
}