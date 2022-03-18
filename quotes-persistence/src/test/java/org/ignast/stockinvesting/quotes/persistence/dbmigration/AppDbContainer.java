package org.ignast.stockinvesting.quotes.persistence.dbmigration;

import javax.sql.DataSource;
import lombok.val;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.MySQLContainer;

public final class AppDbContainer {

    private static final String USERNAME = "test";

    private static final String PASSWORD = "test";

    private static final String DATABASE_NAME = "testschema";

    private static final MySQLContainer CONTAINER = new MySQLContainer("mysql:8.0.28-debian")
        .withDatabaseName(DATABASE_NAME)
        .withUsername(USERNAME)
        .withPassword(PASSWORD);

    private AppDbContainer() {}

    public static MySQLContainer singleton() {
        return CONTAINER;
    }

    public static DataSource getDataSourceTo(final MySQLContainer mysql) {
        final val dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(mysql.getJdbcUrl());
        dataSource.setUsername(mysql.getUsername());
        dataSource.setPassword(mysql.getPassword());
        return dataSource;
    }
}
