package org.ignast.stockinvesting.estimates.persistence;

import org.ignast.stockinvesting.estimates.dbmigration.ProductionDatabaseMigrationVersions;
import org.ignast.stockinvesting.estimates.domain.Company;
import org.ignast.stockinvesting.estimates.service.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public final class CompanyPersistenceTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void test() {
        companyRepository.save(new Company("Amazon", "US", Currency.getInstance("USD")));
        assertThat(companyRepository.findById("AAA")).isNotNull();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public FlywayConfigurationCustomizer migratingToTargetVersion() {
            return c -> c.target(ProductionDatabaseMigrationVersions.TARGET);
        }
    }
}