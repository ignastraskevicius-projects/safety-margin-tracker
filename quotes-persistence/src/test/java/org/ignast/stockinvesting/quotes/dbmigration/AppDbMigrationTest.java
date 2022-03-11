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

import static java.lang.String.format;
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
            val insertAmazon = "INSERT INTO company (id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183124','Amazon','AMZN','XNYS')";
            db.execute(insertAmazon);
        }

        @Test
        public void shouldRejectCompanyWithSameId() {
            val id = "34ab52b2-a169-4ef0-82e9-bb3ce6183124";
            val insertAmazon = format("INSERT INTO company (id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES ('%s','Amazon','AMZN','XNYS')", id);
            val insertHsbc = format("INSERT INTO company (id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES ('%s','Santander','HSBC','XLON')", id);

            db.execute(insertAmazon);
            assertThatExceptionOfType(DuplicateKeyException.class).isThrownBy(() ->
                            db.execute(insertHsbc)).withRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class)
                    .havingRootCause().withMessageContaining("Duplicate entry '34ab52b2-a169-4ef0-82e9-bb3ce6183124' for key 'company.PRIMARY'");
        }

        @Test
        public void shouldRejectCompanyWithAlreadyExistingListing() {
            val symbol = "AMZN";
            val mic = "XNYS";
            val insertAmazonInUs = format("INSERT INTO company (id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183124','Amazon','%s','%s')", symbol, mic);
            val insertAnotherAmazonInUs = format("INSERT INTO company (id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183127','Microsoft','%s','%s')", symbol, mic);

            db.execute(insertAmazonInUs);
            assertThatExceptionOfType(DuplicateKeyException.class).isThrownBy(() -> db.execute(insertAnotherAmazonInUs))
                    .havingRootCause().withMessageContainingAll("Duplicate entry 'AMZN-XNYS' for key 'company.unique_listing'");
        }

        @Test
        public void shouldAcceptCompaniesWithSameSymbolInDifferentMarkets() {
            val symbol = "X";
            val insertXsymbolInTse = format("INSERT INTO company (id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183124','TMX group','%s','XTSX')", symbol);
            val insertXsymbolInNyse = format("INSERT INTO company (id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183127','United States steel corporation','%s','XNYS')", symbol);

            db.execute(insertXsymbolInTse);
            db.execute(insertXsymbolInNyse);
        }

        @Test
        public void shouldAcceptCompaniesWithDifferentSymbolsInOneMarket() {
            val mic = "XNAS";
            val insertAmazon = format("INSERT INTO company (id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183124','Amazon','AMZN','%s')", mic);
            val insertMicrosoft = format("INSERT INTO company (id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183127','Microsoft','MSFT','%s')", mic);

            db.execute(insertAmazon);
            db.execute(insertMicrosoft);
        }

        @Test
        public void shouldPermitLongEnoughCompanyNames() {
            val notTooLongName = "c".repeat(255);
            db.execute(format("INSERT INTO company (id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183125','%s','FR','{}')", notTooLongName));
        }

        @Test
        public void shouldPermit6charStockSymbols() {
            val notTooLongSymbol = "123456";
            db.execute(format("INSERT INTO company (id, company_name, stock_symbol, market_identifier_code) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183126','AMZN','%s','XNAS')", notTooLongSymbol));
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

