package org.ignast.stockinvesting.quotes.dbmigration;

import lombok.val;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.dbmigration.AppDbContainer.getDataSourceTo;

@Testcontainers
public class AppDbMigrationTest {

    @Container
    private static final MySQLContainer appDb = AppDbContainer.singleton();

    private static JdbcTemplate db;

    @BeforeAll
    public static void setup() {
        val dataSource = getDataSourceTo(appDb);
        db = new JdbcTemplate(dataSource);
    }

    @Nested
    class V1CurrentProductionState {
        @BeforeEach
        public void setup() {
            Flyway.configure().dataSource(getDataSourceTo(appDb)).target("1").load().migrate();
        }

        @AfterEach
        public void teardown() {
            Flyway.configure().dataSource(getDataSourceTo(appDb)).load().migrate();
            val jdbcTemplate = new JdbcTemplate(getDataSourceTo(appDb));
            jdbcTemplate.execute("DROP TABLE flyway_schema_history;");
        }

        @Test
        public void shouldNotContainCompanyTable(){
            MysqlAssert.assertThat(db).notContainsTable("company");
        }
    }

    @Nested
    class V2CompanyTableIntroduced {

        @BeforeEach
        public void setup() {
            Flyway.configure().dataSource(getDataSourceTo(appDb)).target("2").load().migrate();
        }

        @AfterEach
        public void teardown() {
            Flyway.configure().dataSource(getDataSourceTo(appDb)).load().migrate();
            val jdbcTemplate = new JdbcTemplate(getDataSourceTo(appDb));
            jdbcTemplate.execute("DROP TABLE flyway_schema_history;");
        }

        @Test
        public void shouldContainCompanyTable() {
            MysqlAssert.assertThat(db).containsTable("company");
        }

        @Test
        public void shouldAcceptCompany() {
            val insertAmazon = "INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (1,'Amazon','AMZN','XNYS')";
            db.execute(insertAmazon);
        }

        @Test
        public void shouldAutoincrementId() {
            val insertAmazon = "INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (15,'Amazon','AMZN','XNAS')";
            val insertMicrosoft = "INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (16,'Microsoft','MSFT','XNAS')";
            db.execute(insertAmazon);
            db.execute(insertMicrosoft);

            Integer amazonId = db.queryForObject("SELECT id FROM company WHERE external_id = 15", Integer.class);
            Integer microsoftId = db.queryForObject("SELECT id FROM company WHERE external_id = 16", Integer.class);
            assertThat(amazonId).isGreaterThan(0);
            assertThat(microsoftId).isGreaterThan(0);
            assertThat(amazonId + 1).isEqualTo(microsoftId);
        }

        @Test
        public void shouldRejectCompanyWithSameInternalId() {
            val externalId = 1;
            val insertAmazon = format("INSERT INTO company (id, external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (1, 2,'Amazon','AMZN','XNAS')", externalId);
            val insertHsbc = format("INSERT INTO company (id, external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (1, 3,'Santander','HSBC','XLON')", externalId);

            db.execute(insertAmazon);
            assertThatExceptionOfType(DuplicateKeyException.class).isThrownBy(() ->
                            db.execute(insertHsbc)).withRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class)
                    .havingRootCause().withMessageContaining("Duplicate entry '1' for key 'company.PRIMARY'");
        }

        @Test
        public void shouldRejectCompanyWithSameExternalId() {
            val externalId = 1;
            val insertAmazon = format("INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (%d,'Amazon','AMZN','XNYS')", externalId);
            val insertHsbc = format("INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (%d,'Santander','HSBC','XLON')", externalId);

            db.execute(insertAmazon);
            assertThatExceptionOfType(DuplicateKeyException.class).isThrownBy(() ->
                            db.execute(insertHsbc)).withRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class)
                    .havingRootCause().withMessageContaining("Duplicate entry '1' for key 'company.unique_external_id'");
        }

        @Test
        public void shouldRejectCompanyWithAlreadyExistingListing() {
            val symbol = "AMZN";
            val mic = "XNYS";
            val insertAmazonInUs = format("INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (1,'Amazon','%s','%s')", symbol, mic);
            val insertAnotherAmazonInUs = format("INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (2,'Microsoft','%s','%s')", symbol, mic);

            db.execute(insertAmazonInUs);
            assertThatExceptionOfType(DuplicateKeyException.class).isThrownBy(() -> db.execute(insertAnotherAmazonInUs))
                    .havingRootCause().withMessageContainingAll("Duplicate entry 'AMZN-XNYS' for key 'company.unique_listing'");
        }

        @Test
        public void shouldAcceptCompaniesWithSameSymbolInDifferentMarkets() {
            val symbol = "X";
            val insertXsymbolInTse = format("INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (1,'TMX group','%s','XTSX')", symbol);
            val insertXsymbolInNyse = format("INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (2,'United States steel corporation','%s','XNYS')", symbol);

            db.execute(insertXsymbolInTse);
            db.execute(insertXsymbolInNyse);
        }

        @Test
        public void shouldAcceptCompaniesWithDifferentSymbolsInOneMarket() {
            val mic = "XNAS";
            val insertAmazon = format("INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (1,'Amazon','AMZN','%s')", mic);
            val insertMicrosoft = format("INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (2,'Microsoft','MSFT','%s')", mic);

            db.execute(insertAmazon);
            db.execute(insertMicrosoft);
        }

        @Test
        public void shouldPermitLongEnoughCompanyNames() {
            val notTooLongName = "c".repeat(255);
            db.execute(format("INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (1,'%s','FR','{}')", notTooLongName));
        }

        @Test
        public void shouldPermit6charStockSymbols() {
            val notTooLongSymbol = "123456";
            db.execute(format("INSERT INTO company (external_id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES (1,'AMZN','%s','XNAS')", notTooLongSymbol));
        }
    }

    @Nested
    class V3 {
        @BeforeEach
        public void setup() {
            Flyway.configure().dataSource(getDataSourceTo(appDb)).target("3").load().migrate();
        }

        @AfterEach
        public void teardown() {
            Flyway.configure().dataSource(getDataSourceTo(appDb)).load().migrate();
            val jdbcTemplate = new JdbcTemplate(getDataSourceTo(appDb));
            jdbcTemplate.execute("DROP TABLE flyway_schema_history;");
        }

        @Test
        public void shouldRevertCompanyTableCreation() {
            MysqlAssert.assertThat(db).notContainsTable("company");
        }
    }
}

