package org.ignast.stockinvesting.estimates.dbmigration;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class CassandraMigrationTest {

    @Container
    private static final GenericContainer cassandra = new CassandraContainer("cassandra:4.0.3").withExposedPorts(9042);

    private static CqlSession session;

    @BeforeAll
    public static void setup() {
        val port = cassandra.getMappedPort(9042);
        new CognitorLibIdempotentMigration(port, "testkeyspace").migrateEnsuringIdempotency(new CognitorLibCassandraMigration());
        session = new CassandraSessions(port, "testkeyspace").newSession();
    }

    @AfterAll
    public static void teardown() {
        session.close();
    }

    @Test
    public void shouldCreateCompanyTable() {
            ResultSet result = session.execute("DESCRIBE TABLES;");

            assertThat(result.all().stream().map(r -> r.getString("name"))).contains("company");
    }
}

