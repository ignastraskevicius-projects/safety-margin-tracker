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

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.quotes.persistence.DomainFactoryForTests.*;
import static org.mockito.Mockito.mock;

@DataJpaTest
class CompanyPersistenceTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void test() {
        val stockExchanges = new StockExchanges(mock(QuotesRepository.class));
        val id = randomUUID();
        companyRepository.save(new Company(id, anyCompanyName(), anySymbol(), stockExchanges.getFor(anyMIC())));
        assertThat(companyRepository.findById(id)).isNotNull();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public FlywayConfigurationCustomizer migratingToTargetVersion() {
            return c -> c.target(ProductionDatabaseMigrationVersions.TARGET);
        }
    }
}