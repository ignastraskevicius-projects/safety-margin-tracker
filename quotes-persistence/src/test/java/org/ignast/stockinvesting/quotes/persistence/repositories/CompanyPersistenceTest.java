package org.ignast.stockinvesting.quotes.persistence.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.persistence.testutil.DomainFactoryForTests.anyQuotes;

import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.val;
import org.ignast.stockinvesting.quotes.domain.Company;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.quotes.domain.CompanyRepository;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockExchange;
import org.ignast.stockinvesting.quotes.domain.StockExchanges;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.ignast.stockinvesting.quotes.persistence.dbmigration.ProductionDatabaseMigrationVersions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TestTransaction;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SuppressWarnings("checkstyle:innertypelast")
final class CompanyPersistenceTest {

    @Autowired
    private CompanyRepository companyRepository;

    private final JdbcTemplate jdbcTemplate;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public FlywayConfigurationCustomizer migratingToTargetVersion() {
            return c -> c.target(ProductionDatabaseMigrationVersions.TARGET);
        }
    }

    public CompanyPersistenceTest(@Autowired final DataSource dataSource) throws SQLException {
        final val testDataSource = new SingleConnectionDataSource();
        testDataSource.setUrl(dataSource.getConnection().getMetaData().getURL());
        testDataSource.setUsername(dataSource.getConnection().getMetaData().getUserName());
        jdbcTemplate = new JdbcTemplate(testDataSource);
    }

    @Test
    public void shouldInsertCompany() {
        final StockExchange nasdaq = new StockExchanges(anyQuotes()).getFor(new MarketIdentifierCode("XNAS"));

        companyRepository.save(
            Company.create(
                new CompanyExternalId(6),
                new CompanyName("Amazon"),
                new StockSymbol("AMZN"),
                nasdaq
            )
        );
        commit();

        final val result = jdbcTemplate.queryForMap("SELECT * FROM company;");
        assertThat((Integer) result.get("id")).isNotNull().isGreaterThan(0);
        assertThat(result.get("external_id")).isEqualTo(6);
        assertThat(result.get("company_name")).isEqualTo("Amazon");
        assertThat(result.get("stock_symbol")).isEqualTo("AMZN");
        assertThat(result.get("market_identifier_code")).isEqualTo("XNAS");
    }

    @Test
    public void shouldFindCompanyByExternalId() {
        jdbcTemplate.execute(
            "INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
            "VALUES (16, 'Amazon', 'AMZN', 'XNAS')"
        );

        final val company = companyRepository.findByExternalId(new CompanyExternalId(16));
        assertThat(company).isPresent();
        company.ifPresent(c -> {
            assertThat(c.getExternalId()).isEqualTo(new CompanyExternalId(16));
            assertThat(c.getName()).isEqualTo(new CompanyName("Amazon"));
            assertThat(c.getStockSymbol()).isEqualTo(new StockSymbol("AMZN"));
            assertThat(c.getStockExchange().getMarketIdentifierCode())
                .isEqualTo(new MarketIdentifierCode("XNAS"));
        });
    }

    @Test
    public void shouldRejectCompanyUnderExistingListing() {
        final val nasdaq = new StockExchanges(anyQuotes()).getFor(new MarketIdentifierCode("XNAS"));
        final val amazonSymbol = new StockSymbol("AMZN");
        final val amazon1 = Company.create(
            new CompanyExternalId(6),
            new CompanyName("Amazon1"),
            amazonSymbol,
            nasdaq
        );
        final val amazon2 = Company.create(
            new CompanyExternalId(6),
            new CompanyName("Amazon2"),
            amazonSymbol,
            nasdaq
        );

        companyRepository.save(amazon1);
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> companyRepository.save(amazon2))
            .withMessageContaining("UNIQUE_LISTING");
    }

    @Test
    public void shouldRejectCompanyWithExistingExternalId() {
        final val externalId = new CompanyExternalId(6);
        final val hkex = new StockExchanges(anyQuotes()).getFor(new MarketIdentifierCode("XHKG"));
        final val amazon1 = Company.create(
            externalId,
            new CompanyName("Alibaba1"),
            new StockSymbol("BABA"),
            hkex
        );
        final val amazon2 = Company.create(
            externalId,
            new CompanyName("Alibaba2"),
            new StockSymbol("9988"),
            hkex
        );

        companyRepository.save(amazon1);
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> companyRepository.save(amazon2))
            .withMessageContaining("UNIQUE_EXTERNAL_ID");
    }

    private void commit() {
        TestTransaction.flagForCommit();
        TestTransaction.end();
    }
}
