package org.ignast.stockinvesting.estimates.dbmigration;

import com.datastax.oss.driver.api.core.CqlSession;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.InetSocketAddress;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils.randomAlphabetic;

class CassandraSessions {
    private final int port;
    private final String keyspace;

    public CqlSession newSession() {
        val session = CqlSession.builder().addContactPoint(new InetSocketAddress(port)).withLocalDatacenter("datacenter1").build();
        session.execute(format("CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class':'SimpleStrategy','replication_factor':'1'}", keyspace));
        session.execute(format("USE %s;", keyspace));
        return session;
    }

    CassandraSessions(int port, String keyspace) {
        this.port = port;
        this.keyspace = keyspace;
    }
}

@Testcontainers
class CassandraSessionsTest {

    @Container
    private static final GenericContainer cassandra = new CassandraContainer("cassandra:4.0.3").withExposedPorts(9042);

    private static final String KEY_SPACE_NAME = "testkeyspace_" + randomAlphabetic(3).toLowerCase();

    private int port;

    @BeforeEach
    public void setup() {
        port = cassandra.getMappedPort(9042);
        deleteKeyspace(KEY_SPACE_NAME);

    }

    @Test
    public void shouldCreateKeyspaceIfDoesNotExist() {
        try (val session = new CassandraSessions(port, KEY_SPACE_NAME).newSession()) {
            val result = session.execute("SELECT * FROM system_schema.keyspaces;");

            assertThat(result.all().stream().map(r -> r.getString("keyspace_name"))).contains(KEY_SPACE_NAME);
        };
    }

    @Test
    public void shouldPreserveCustomPreparationInSubsequentSession() {
        try (val session1 = new CassandraSessions(port, KEY_SPACE_NAME).newSession()) {
            session1.execute("CREATE TABLE customtable (id uuid PRIMARY KEY);");
        }
        try (val session2 = new CassandraSessions(port, KEY_SPACE_NAME).newSession()) {
            val result = session2.execute("DESCRIBE TABLES;");

            assertThat(result.all().stream().map(r -> r.getString("keyspace_name") + "." + r.getString("name"))).contains(KEY_SPACE_NAME + ".customtable");
        }
    }

    @Test
    public void shouldOpenMultipleSessions() {
        val sessions = new CassandraSessions(port, KEY_SPACE_NAME);

        sessions.newSession().close();
        try (val session2 = sessions.newSession()) {

            assertThat(session2.execute("DESCRIBE KEYSPACES;").all()).isNotEmpty();
        }
    }

    private void deleteKeyspace(String keyspace) {
        try (val session = new CassandraSessions(port, KEY_SPACE_NAME).newSession()) {
            session.execute(format("DROP KEYSPACE %s;", keyspace));
        };
    }
}