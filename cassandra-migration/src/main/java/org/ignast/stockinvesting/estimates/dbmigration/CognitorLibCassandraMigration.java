package org.ignast.stockinvesting.estimates.dbmigration;

import com.datastax.oss.driver.api.core.CqlSession;
import org.cognitor.cassandra.migration.Database;
import org.cognitor.cassandra.migration.MigrationConfiguration;
import org.cognitor.cassandra.migration.MigrationRepository;
import org.cognitor.cassandra.migration.MigrationTask;

interface Migration {
    void migrate(CqlSession session, String keyspace);
}

class CognitorLibCassandraMigration implements Migration {
    @Override
    public void migrate(CqlSession session, String keyspace) {
        Database database = new Database(session, new MigrationConfiguration().withKeyspaceName(keyspace));
        MigrationTask migration = new MigrationTask(database, new MigrationRepository());
        migration.migrate();
    }
}
