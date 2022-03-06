package org.ignast.stockinvesting.estimates.dbmigration;

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
import static org.ignast.stockinvesting.estimates.dbmigration.AppDbContainer.getDataSourceTo;

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
            val insertAmazon = "INSERT INTO company (id, name, country, company_json) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183124','Amazon','US','{}')";
            db.execute(insertAmazon);
        }

        @Test
        public void shouldRejectCompanyWithSameId() {
            val id = "34ab52b2-a169-4ef0-82e9-bb3ce6183124";
            val insertAmazon = format("INSERT INTO company (id, name, country, company_json) " +
                    "VALUES ('%s','Amazon','US','{}')", id);
            val insertSantander = format("INSERT INTO company (id, name, country, company_json) " +
                    "VALUES ('%s','Santander','UK','{}')", id);

            db.execute(insertAmazon);
            assertThatExceptionOfType(DuplicateKeyException.class).isThrownBy(() ->
                            db.execute(insertSantander)).withRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class)
                    .havingRootCause().withMessageContaining("Duplicate entry '34ab52b2-a169-4ef0-82e9-bb3ce6183124' for key 'company.PRIMARY'");
        }

        @Test
        public void shouldRejectCompanyWithAlreadyExistingNameInTheCountry() {
            val name = "Amazon";
            val country = "US";
            val insertAmazonInUs = format("INSERT INTO company (id, name, country, company_json) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183124','%s','%s','{}')", name, country);
            val insertAnotherAmazonInUs = format("INSERT INTO company (id, name, country, company_json) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183127','%s','%s','{}')", name, country);

            db.execute(insertAmazonInUs);
            assertThatExceptionOfType(DuplicateKeyException.class).isThrownBy(() -> db.execute(insertAnotherAmazonInUs))
                    .havingRootCause().withMessageContainingAll("Duplicate entry 'Amazon-US' for key 'company.unique_name_in_country'");
        }

        @Test
        public void shouldAcceptCompaniesWithSameNameInDifferentCountries() {
            val name = "Santander";
            val insertSantanderInUk = format("INSERT INTO company (id, name, country, company_json) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183124','%s','UK','{}')", name);
            val insertSantanderInSpain = format("INSERT INTO company (id, name, country, company_json) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183127','%s','ES','{}')", name);

            db.execute(insertSantanderInUk);
            db.execute(insertSantanderInSpain);
        }

        @Test
        public void shouldAcceptCompaniesWithDifferentNamesInOneCountry() {
            val county = "US";
            val insertAmazon = format("INSERT INTO company (id, name, country, company_json) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183124','Amazon','%s','{}')", county);
            val insertMicrosoft = format("INSERT INTO company (id, name, country, company_json) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183127','Microsoft','ES','{}')", county);

            db.execute(insertAmazon);
            db.execute(insertMicrosoft);
        }

        @Test
        public void shouldPermitLongEnoughCompanyNames() {
            val notTooLongName = "c".repeat(255);
            db.execute(format("INSERT INTO company (id, name, country, company_json) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183125','%s','FR','{}')", notTooLongName));
        }

        @Test
        public void shouldPermitLongEnoughJson() {
            val notTooLongName = "c".repeat(255);
            val notTooLongJson = format("{\"id\":\"34ab52b2-a169-4ef0-82e9-bb3ce6183126\",\"name\":\"%s\",\"country\":\"US\",\"functionalCurrency\":\"USD\",\"marketIdentifierCode\":\"XNYS\",\"ticker\":\"AMZNW\"}", notTooLongName);
            db.execute(format("INSERT INTO company (id, name, country, company_json) " +
                    "VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183126','Santander','ES','%s')", notTooLongJson));
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

