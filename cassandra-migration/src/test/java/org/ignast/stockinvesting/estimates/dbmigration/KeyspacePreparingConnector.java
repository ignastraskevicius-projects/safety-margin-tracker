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

class KeyspacePreparingConnector {
    private final CqlSession session;

    public CqlSession getSession() {
        return session;
    }

    KeyspacePreparingConnector(int port, String keyspace) {
        session = CqlSession.builder().addContactPoint(new InetSocketAddress(port)).withLocalDatacenter("datacenter1").build();
        session.execute(format("CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class':'SimpleStrategy','replication_factor':'1'}", keyspace));
    }

    public void close() {
        session.close();
    }
}

@Testcontainers
class KeyspacePreparingConnectorTest {

    @Container
    private static final GenericContainer cassandra = new CassandraContainer("cassandra:4.0.3").withExposedPorts(9042);

    private static final String KEY_SPACE_NAME = "testkeyspace_" + randomAlphabetic(3).toLowerCase();

    private int port;

    @BeforeEach
    public void setup() {
        port = cassandra.getMappedPort(9042);
    }

    @Test
    public void shouldCreateKeyspaceIfDoesNotExist() {
        KeyspacePreparingConnector connector = new KeyspacePreparingConnector(port, KEY_SPACE_NAME);

        val result = connector.getSession().execute("SELECT * FROM system_schema.keyspaces;");
        assertThat(result.all().stream().map(r -> r.getString("keyspace_name"))).contains(KEY_SPACE_NAME);

        connector.close();
    }

    @Test
    public void shouldIgnoreCreatingKeyspaceIfExist() {
        KeyspacePreparingConnector connector1 = new KeyspacePreparingConnector(port, KEY_SPACE_NAME);
        KeyspacePreparingConnector connector2 = new KeyspacePreparingConnector(port, KEY_SPACE_NAME);

        val result = connector2.getSession().execute("SELECT * FROM system_schema.keyspaces;");

        assertThat(result.all().stream().map(r -> r.getString("keyspace_name"))).contains(KEY_SPACE_NAME);

        connector2.close();
        connector1.close();
    }
}