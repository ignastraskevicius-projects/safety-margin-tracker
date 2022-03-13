package org.ignast.stockinvesting.quotes.persistence;

import lombok.val;
import org.h2.tools.Server;
import org.ignast.stockinvesting.quotes.*;
import org.ignast.stockinvesting.quotes.dbmigration.ProductionDatabaseMigrationVersions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.transaction.TestTransaction;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.quotes.persistence.DomainFactoryForTests.*;
import static org.mockito.Mockito.mock;

@DataJpaTest
class CompanyPersistenceTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    public CompanyPersistenceTest(@Autowired DataSource dataSource) throws SQLException {
        val testDataSource = new SingleConnectionDataSource();
        testDataSource.setUrl(dataSource.getConnection().getMetaData().getURL());
        testDataSource.setUsername(dataSource.getConnection().getMetaData().getUserName());
        jdbcTemplate = new JdbcTemplate(testDataSource);
    }

    @Test
    public void shouldInsertCompany() {
        StockExchange nasdaq = new StockExchanges(mock(QuotesRepository.class)).getFor(new MarketIdentifierCode("XNAS"));

        companyRepository.save(new Company(new PositiveNumber(6), new CompanyName("Amazon"), new StockSymbol("AMZN"), nasdaq));
        commit();

        val result = jdbcTemplate.queryForMap("SELECT * FROM company;");
        assertThat((Integer) result.get("id")).isNotNull().isGreaterThan(0);
        assertThat(result.get("external_id")).isEqualTo(6);
        assertThat(result.get("company_name")).isEqualTo("Amazon");
        assertThat(result.get("stock_symbol")).isEqualTo("AMZN");
        assertThat(result.get("market_identifier_code")).isEqualTo("XNAS");
    }

    private void commit() {
        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public FlywayConfigurationCustomizer migratingToTargetVersion() {
            return c -> c.target(ProductionDatabaseMigrationVersions.TARGET);
        }
    }
}