package org.ignast.stockinvesting.quotes.integrationtest;

import lombok.val;
import org.ignast.stockinvesting.quotes.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.transaction.TestTransaction;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DockerizedDevMysqlIT {

    @Container
    public static final MySQLContainer mysql = new MySQLContainer(DockerImageName.parse("org.ignast.stock-investing.quotes/mysql-dev:1.0-SNAPSHOT").asCompatibleSubstituteFor("mysql")).withPassword("test");

    @Autowired
    private CompanyRepository companyRepository;

    @DynamicPropertySource
    private static void registerDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mysql.getJdbcUrl().replaceFirst("/test", "/quotes"));
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "test");
    }

    @Test
    public void shouldCreateCompany() {
        companyRepository.save(new Company(new CompanyExternalId(3), new CompanyName("Amazon"), new StockSymbol("AMZN"), new StockExchanges(mock(QuotesRepository.class)).getFor(new MarketIdentifierCode("XNAS"))));
        commit();
        val result = companyRepository.findByExternalId(new CompanyExternalId(3));

        assertThat(result.isPresent());
        result.stream().forEach(c -> assertThat(c.getName().get()).isEqualTo("Amazon"));
    }

    private void commit() {
        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        FlywayMigrationStrategy noMigration() {
            return (f) -> {};
        }
    }
}
