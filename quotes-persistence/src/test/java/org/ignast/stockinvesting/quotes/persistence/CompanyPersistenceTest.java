package org.ignast.stockinvesting.quotes.persistence;

import lombok.val;
import org.ignast.stockinvesting.quotes.*;
import org.ignast.stockinvesting.quotes.dbmigration.ProductionDatabaseMigrationVersions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.Map;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.quotes.persistence.DomainFactoryForTests.*;
import static org.mockito.Mockito.mock;

@DataJpaTest
class CompanyPersistenceTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test() {
        val stockExchanges = new StockExchanges(mock(QuotesRepository.class));
        companyRepository.save(new Company(6, anyCompanyName(), anySymbol(), stockExchanges.getFor(anyMIC())));
        TestTransaction.flagForCommit();
        TestTransaction.end();
        val result = jdbcTemplate.queryForMap("SELECT external_id FROM company;");
        assertThat(result.get("external_id"));

        assertThat(companyRepository.findById(6)).isNotNull();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public FlywayConfigurationCustomizer migratingToTargetVersion() {
            return c -> c.target(ProductionDatabaseMigrationVersions.TARGET);
        }
    }
}