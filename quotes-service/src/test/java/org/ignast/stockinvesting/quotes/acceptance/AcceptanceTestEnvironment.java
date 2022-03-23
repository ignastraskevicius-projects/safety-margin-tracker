package org.ignast.stockinvesting.quotes.acceptance;

import static java.lang.String.format;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class AcceptanceTestEnvironment {

    private static final int EXPOSED_ALPHAVANTAGE_PORT = 8080;

    @Container
    private static final GenericContainer ALPHAVANTAGE = new GenericContainer(
        DockerImageName.parse(System.getProperty("alphavantage.image"))
    )
        .withExposedPorts(EXPOSED_ALPHAVANTAGE_PORT);

    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer(
        DockerImageName.parse(System.getProperty("mysqldev.image")).asCompatibleSubstituteFor("mysql")
    )
        .withPassword("test");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void cleanupDatabase() {
        jdbcTemplate.execute("DELETE FROM company;");
    }

    @DynamicPropertySource
    private static void registedDatasource(final DynamicPropertyRegistry registry) {
        registry.add("alphavantage.url", () -> format("http://localhost:%d", getAlphavantagePort()));
        registry.add("spring.datasource.url", () -> MYSQL.getJdbcUrl().replace("/test", "/quotes"));
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "test");
    }

    private static Integer getAlphavantagePort() {
        return ALPHAVANTAGE.getMappedPort(EXPOSED_ALPHAVANTAGE_PORT);
    }
}
