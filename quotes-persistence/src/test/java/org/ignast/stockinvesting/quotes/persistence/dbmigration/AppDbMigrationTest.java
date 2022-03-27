package org.ignast.stockinvesting.quotes.persistence.dbmigration;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.persistence.dbmigration.AppDbContainer.getDataSourceTo;

import java.sql.SQLIntegrityConstraintViolationException;
import lombok.val;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SuppressWarnings("checkstyle:hideutilityclassconstructor")
public final class AppDbMigrationTest {

    @Container
    @SuppressWarnings("rawtypes")
    private static final MySQLContainer APP_DB = AppDbContainer.singleton();

    private static final int MAX_COMPANY_NAME_LENGTH = 160;

    private static JdbcTemplate db;

    @BeforeAll
    public static void setup() {
        final val dataSource = getDataSourceTo(APP_DB);
        db = new JdbcTemplate(dataSource);
    }

    @Nested
    final class V1CurrentProductionState {

        @BeforeEach
        public void setup() {
            Flyway.configure().dataSource(getDataSourceTo(APP_DB)).target("1").load().migrate();
        }

        @AfterEach
        public void teardown() {
            Flyway.configure().dataSource(getDataSourceTo(APP_DB)).load().migrate();
            final val jdbcTemplate = new JdbcTemplate(getDataSourceTo(APP_DB));
            jdbcTemplate.execute("DROP TABLE flyway_schema_history;");
        }

        @Test
        public void shouldNotContainCompanyTable() {
            MysqlAssert.assertThat(db).notContainsTable("company");
        }
    }

    @Nested
    final class V2CompanyTableIntroduced {

        @BeforeEach
        public void setup() {
            Flyway.configure().dataSource(getDataSourceTo(APP_DB)).target("2").load().migrate();
        }

        @AfterEach
        public void teardown() {
            Flyway.configure().dataSource(getDataSourceTo(APP_DB)).load().migrate();
            final val jdbcTemplate = new JdbcTemplate(getDataSourceTo(APP_DB));
            jdbcTemplate.execute("DROP TABLE flyway_schema_history;");
        }

        @Test
        public void shouldContainCompanyTable() {
            MysqlAssert.assertThat(db).containsTable("company");
        }

        @Test
        public void shouldAcceptCompany() {
            final val insertAmazon =
                """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (1,'Amazon','AMZN','XNYS')""";
            db.execute(insertAmazon);
        }

        @Test
        public void shouldAutoincrementId() {
            final val insertAmazon =
                """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (15,'Amazon','AMZN','XNAS')""";
            final val insertMicrosoft =
                """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (16,'Microsoft','MSFT','XNAS')""";
            db.execute(insertAmazon);
            db.execute(insertMicrosoft);

            final val amazonId = db.queryForObject(
                "SELECT id FROM company WHERE external_id = 15",
                Integer.class
            );
            final val microsoftId = db.queryForObject(
                "SELECT id FROM company WHERE external_id = 16",
                Integer.class
            );
            assertThat(amazonId).isGreaterThan(0);
            assertThat(microsoftId).isGreaterThan(0);
            assertThat(amazonId + 1).isEqualTo(microsoftId);
        }

        @Test
        public void shouldRejectCompanyWithSameInternalId() {
            final val externalId = 1;
            final val insertAmazon = format(
                """
                    INSERT INTO company (id, external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (1, 2,'Amazon','AMZN','XNAS')""",
                externalId
            );
            final val insertHsbc = format(
                """
                    INSERT INTO company (id, external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (1, 3,'Santander','HSBC','XLON')""",
                externalId
            );

            db.execute(insertAmazon);
            assertThatExceptionOfType(DuplicateKeyException.class)
                .isThrownBy(() -> db.execute(insertHsbc))
                .withRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class)
                .havingRootCause()
                .withMessageContaining("Duplicate entry '1' for key 'company.PRIMARY'");
        }

        @Test
        public void shouldRejectCompanyWithSameExternalId() {
            final val externalId = 1;
            final val insertAmazon = format(
                """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (%d,'Amazon','AMZN','XNYS')""",
                externalId
            );
            final val insertHsbc = format(
                """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (%d,'Santander','HSBC','XLON')""",
                externalId
            );

            db.execute(insertAmazon);
            assertThatExceptionOfType(DuplicateKeyException.class)
                .isThrownBy(() -> db.execute(insertHsbc))
                .withRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class)
                .havingRootCause()
                .withMessageContaining("Duplicate entry '1' for key 'company.unique_external_id'");
        }

        @Test
        public void shouldRejectCompanyWithAlreadyExistingListing() {
            final val symbol = "AMZN";
            final val mic = "XNYS";
            final val insertAmazonInUs = format(
                """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (1,'Amazon','%s','%s')""",
                symbol,
                mic
            );
            final val insertAnotherAmazonInUs = format(
                """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (2,'Microsoft','%s','%s')""",
                symbol,
                mic
            );

            db.execute(insertAmazonInUs);
            assertThatExceptionOfType(DuplicateKeyException.class)
                .isThrownBy(() -> db.execute(insertAnotherAmazonInUs))
                .havingRootCause()
                .withMessageContainingAll("Duplicate entry 'AMZN-XNYS' for key 'company.unique_listing'");
        }

        @Test
        public void shouldRejectCompanyWithSameExternalIdEvenIfListingInformationIsDuplicateToo() {
            final val externalId = 1;
            final val insertAmazon = format(
                """
                        INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                        VALUES (2,'Amazon','AMZN','XNAS')""",
                externalId
            );
            final val insertHsbc = format(
                """
                        INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                        VALUES (2,'Santander','AMZN','XNAS')""",
                externalId
            );

            db.execute(insertAmazon);
            assertThatExceptionOfType(DuplicateKeyException.class)
                .isThrownBy(() -> db.execute(insertHsbc))
                .withRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class)
                .havingRootCause()
                .withMessageContaining("Duplicate entry '2' for key 'company.unique_external_id'");
        }

        @Test
        public void shouldAcceptCompaniesWithSameSymbolInDifferentMarkets() {
            final val symbol = "X";
            final val insertXsymbolInTse = format(
                """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (1,'TMX group','%s','XTSX')""",
                symbol
            );
            final val insertXsymbolInNyse = format(
                """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (2,'United States steel corporation','%s','XNYS')""",
                symbol
            );

            db.execute(insertXsymbolInTse);
            db.execute(insertXsymbolInNyse);
        }

        @Test
        public void shouldAcceptCompaniesWithDifferentSymbolsInOneMarket() {
            final val mic = "XNAS";
            final val insertAmazon = format(
                """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (1,'Amazon','AMZN','%s')""",
                mic
            );
            final val insertMicrosoft = format(
                """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (2,'Microsoft','MSFT','%s')""",
                mic
            );

            db.execute(insertAmazon);
            db.execute(insertMicrosoft);
        }

        @Test
        public void shouldPermitLongEnoughCompanyNames() {
            final val notTooLongName = "c".repeat(MAX_COMPANY_NAME_LENGTH);
            db.execute(
                format(
                    """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (1,'%s','FR','{}')""",
                    notTooLongName
                )
            );
        }

        @Test
        public void shouldPermit6charStockSymbols() {
            final val notTooLongSymbol = "123456";
            db.execute(
                format(
                    """
                    INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code)
                    VALUES (1,'AMZN','%s','XNAS')""",
                    notTooLongSymbol
                )
            );
        }
    }

    @Nested
    final class V3 {

        @BeforeEach
        public void setup() {
            Flyway.configure().dataSource(getDataSourceTo(APP_DB)).target("3").load().migrate();
        }

        @AfterEach
        public void teardown() {
            Flyway.configure().dataSource(getDataSourceTo(APP_DB)).load().migrate();
            final val jdbcTemplate = new JdbcTemplate(getDataSourceTo(APP_DB));
            jdbcTemplate.execute("DROP TABLE flyway_schema_history;");
        }

        @Test
        public void shouldRevertCompanyTableCreation() {
            MysqlAssert.assertThat(db).notContainsTable("company");
        }
    }
}
