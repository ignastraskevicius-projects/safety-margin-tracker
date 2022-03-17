package org.ignast.stockinvesting.quotes.persistence.integrationtest;

import lombok.val;
import org.ignast.stockinvesting.quotes.domain.Company;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.quotes.domain.CompanyRepository;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockExchanges;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
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
import static org.ignast.stockinvesting.quotes.persistence.testutil.DomainFactoryForTests.anyQuotes;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public final class DockerizedDevMysqlIT {

    @Container
    public static final MySQLContainer MYSQL = new MySQLContainer(DockerImageName.parse("org.ignast.stock-investing.quotes/mysql-dev:1.0-SNAPSHOT").asCompatibleSubstituteFor("mysql")).withPassword("test");

    @Autowired
    private CompanyRepository companyRepository;

    @DynamicPropertySource
    private static void registerDatasource(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> MYSQL.getJdbcUrl().replaceFirst("/test", "/quotes"));
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "test");
    }

    @Test
    public void shouldCreateCompany() {
        companyRepository.save(Company.create(new CompanyExternalId(3), new CompanyName("Amazon"), new StockSymbol("AMZN"),
                new StockExchanges(anyQuotes()).getFor(new MarketIdentifierCode("XNAS"))));
        commit();
        final val result = companyRepository.findByExternalId(new CompanyExternalId(3));

        assertThat(result.isPresent());
        result.ifPresent(c -> assertThat(c.getName().get()).isEqualTo("Amazon"));
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
