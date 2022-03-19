package org.ignast.stockinvesting.estimates.dbmigration;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.estimates.dbmigration.AppDbContainer.getDataSourceTo;

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

@SuppressWarnings("checkstyle:hideutilityclassconstructor")
@Testcontainers
public final class AppDbMigrationTest {

    private static final int MAX_COMPANY_NAME_LENGTH = 160;

    @Container
    private static final MySQLContainer APP_DB = AppDbContainer.singleton();

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
                    INSERT INTO company (id, name, country_code, company_json) 
                    VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183124','Amazon','US','{}')""";
            db.execute(insertAmazon);
        }

        @Test
        public void shouldRejectCompanyWithSameId() {
            final val id = "34ab52b2-a169-4ef0-82e9-bb3ce6183124";
            final val insertAmazon = format(
                """
                    INSERT INTO company (id, name, country_code, company_json) 
                    VALUES ('%s','Amazon','US','{}')""",
                id
            );
            final val insertSantander = format(
                """
                    INSERT INTO company (id, name, country_code, company_json) 
                    VALUES ('%s','Santander','UK','{}')""",
                id
            );

            db.execute(insertAmazon);
            assertThatExceptionOfType(DuplicateKeyException.class)
                .isThrownBy(() -> db.execute(insertSantander))
                .withRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class)
                .havingRootCause()
                .withMessageContaining(
                    "Duplicate entry '34ab52b2-a169-4ef0-82e9-bb3ce6183124' for key 'company.PRIMARY'"
                );
        }

        @Test
        public void shouldRejectCompanyWithAlreadyExistingNameInTheCountry() {
            final val name = "Amazon";
            final val country = "US";
            final val insertAmazonInUs = format(
                """
                    INSERT INTO company (id, name, country_code, company_json) 
                    VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183124','%s','%s','{}')""",
                name,
                country
            );
            final val insertAnotherAmazonInUs = format(
                """
                    INSERT INTO company (id, name, country_code, company_json) 
                    VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183127','%s','%s','{}')""",
                name,
                country
            );

            db.execute(insertAmazonInUs);
            assertThatExceptionOfType(DuplicateKeyException.class)
                .isThrownBy(() -> db.execute(insertAnotherAmazonInUs))
                .havingRootCause()
                .withMessageContainingAll(
                    "Duplicate entry 'Amazon-US' for key 'company.unique_name_in_country'"
                );
        }

        @Test
        public void shouldAcceptCompaniesWithSameNameInDifferentCountries() {
            final val name = "Santander";
            final val insertSantanderInUk = format(
                """
                    INSERT INTO company (id, name, country_code, company_json) 
                    VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183124','%s','UK','{}')""",
                name
            );
            final val insertSantanderInSpain = format(
                """
                    INSERT INTO company (id, name, country_code, company_json)
                    VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183127','%s','ES','{}')""",
                name
            );

            db.execute(insertSantanderInUk);
            db.execute(insertSantanderInSpain);
        }

        @Test
        public void shouldAcceptCompaniesWithDifferentNamesInOneCountry() {
            final val county = "US";
            final val insertAmazon = format(
                """
                    INSERT INTO company (id, name, country_code, company_json) 
                    VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183124','Amazon','%s','{}')""",
                county
            );
            final val insertMicrosoft = format(
                """
                    INSERT INTO company (id, name, country_code, company_json) 
                    VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183127','Microsoft','ES','{}')""",
                county
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
                    INSERT INTO company (id, name, country_code, company_json) 
                    VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183125','%s','FR','{}')""",
                    notTooLongName
                )
            );
        }

        @Test
        public void shouldPermitLongEnoughJson() {
            final val notTooLongName = "c".repeat(MAX_COMPANY_NAME_LENGTH + 1);
            final val notTooLongJson = format(
                """
                        {
                            "id":"34ab52b2-a169-4ef0-82e9-bb3ce6183126",
                            "name":"%s",
                            "country_code":"US",
                            "functionalCurrency":"USD",
                            "marketIdentifierCode":"XNYS",
                            "ticker":"AMZNW"
                        }""",
                notTooLongName
            );
            db.execute(
                format(
                    """
                    INSERT INTO company (id, name, country_code, company_json) 
                    VALUES ('34ab52b2-a169-4ef0-82e9-bb3ce6183126','Santander','ES','%s')""",
                    notTooLongJson
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
