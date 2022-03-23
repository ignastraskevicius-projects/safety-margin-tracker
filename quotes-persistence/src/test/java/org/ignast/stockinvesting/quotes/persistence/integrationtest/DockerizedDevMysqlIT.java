package org.ignast.stockinvesting.quotes.persistence.integrationtest;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public final class DockerizedDevMysqlIT {

    @Container
    public static final MySQLContainer MYSQL = new MySQLContainer(
        DockerImageName.parse(System.getProperty("docker.image")).asCompatibleSubstituteFor("mysql")
    )
        .withPassword("test");

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(createDataSource());

    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldCreateCompany() {
        jdbcTemplate.execute(
            """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) 
                    VALUES (1,'Amazon','AMZN','XNYS')"""
        );

        final val name = jdbcTemplate.queryForObject(
            "SELECT company_name FROM company WHERE external_id = 1",
            String.class
        );

        assertThat(name).isEqualTo("Amazon");
    }

    private DataSource createDataSource() {
        final val dataSource = new SingleConnectionDataSource();
        dataSource.setUrl(MYSQL.getJdbcUrl().replaceFirst("/test", "/quotes"));
        dataSource.setUsername("root");
        dataSource.setPassword("test");
        return dataSource;
    }
}
