package org.ignast.stockinvesting.estimates.dbmigration;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public interface MysqlMigration {
    void migrate(DataSource dataSource);
}

class FlywayMigration implements MysqlMigration {

    public void migrate(DataSource dataSource) {
        Flyway.configure().baselineOnMigrate(true).dataSource(dataSource).load().migrate();
    }
}