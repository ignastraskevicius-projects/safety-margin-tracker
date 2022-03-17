package org.ignast.stockinvesting.quotes.persistence.dbmigration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
public final class FlywayAssert {

    private final JdbcTemplate db;

    public static FlywayAssert assertThat(final JdbcTemplate db) {
        return new FlywayAssert(db);
    }

    public void hasNotJustMigrated(final String expectedVersion) {
        final val lastMigration = queryLastMigration();
        expectNotSqlOperationo(lastMigration);
        expectVersion(expectedVersion, lastMigration);
    }

    public void hasJustMigrated(final String expectedVersion) {
        final val lastMigration = queryLastMigration();
        expectSqlOperation(lastMigration);
        expectVersion(expectedVersion, lastMigration);
    }

    private LastMigration queryLastMigration() {
        final val results = db.queryForMap("SELECT version, type FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 1;");
        return new LastMigration(results.get("type").toString(), results.get("version").toString());
    }

    private void expectVersion(final String expectedVersion, final LastMigration lastMigration) {
        if (!expectedVersion.equals(lastMigration.getVersion())) {
            throw new AssertionError(format("Expected last flyway migration to be '%s' version migration but was '%s'", expectedVersion, lastMigration.getVersion()));
        }
    }

    private void expectSqlOperation(final LastMigration lastMigration) {
        if (!"SQL".equals(lastMigration.getType())) {
            throw new AssertionError(format("Expected last flyway migration type to be 'SQL' but was '%s'", lastMigration.getType()));
        }
    }

    private void expectNotSqlOperationo(final LastMigration lastMigration) {
        if ("SQL".equals(lastMigration.getType())) {
            throw new AssertionError(format("Expected last flyway migration type not to be 'SQL' but was '%s'", lastMigration.getType()));
        }
    }

    @Getter
    @RequiredArgsConstructor
    static class LastMigration {
        private final String type;

        private final String version;
    }
}

final class FlywayAssertLastMigrationTest {

    private static final String QUERY_LAST_APPLIED_MIGRATION = "SELECT version, type FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 1;";

    private final JdbcTemplate db = mock(JdbcTemplate.class);

    @Test
    public void assertingLastMigrationShouldSucceedForExpectedVersion() {
        when(db.queryForMap(QUERY_LAST_APPLIED_MIGRATION)).thenReturn(Map.of("type", "SQL", "version", "V1"));

        FlywayAssert.assertThat(db).hasJustMigrated("V1");
    }

    @Test
    public void assertingLastMigrationShouldFailIfItWasForDifferentVersion() {
        with2DistinctVersions((v1, v2) -> {
            when(db.queryForMap(QUERY_LAST_APPLIED_MIGRATION)).thenReturn(Map.of("type", "SQL", "version", v1));

            assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> FlywayAssert.assertThat(db)
                    .hasJustMigrated(v2))
                    .withMessage(format("Expected last flyway migration to be '%s' version migration but was '%s'", v2, v1));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"UNDO_SQL", "OTHER_TYPE"})
    public void assertingLastMigrationShouldFailIfLastOperationWasNotSql(final String nonSqlType) {
        when(db.queryForMap(QUERY_LAST_APPLIED_MIGRATION)).thenReturn(Map.of("type", nonSqlType, "version", "ANY"));

        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> FlywayAssert.assertThat(db)
                .hasJustMigrated("anyVersion"))
                .withMessage(format("Expected last flyway migration type to be 'SQL' but was '%s'", nonSqlType));
    }

    @Test
    public void assertingLastOperationWasNotMigrationShouldSucceedForExpectedVersion() {
        when(db.queryForMap(QUERY_LAST_APPLIED_MIGRATION)).thenReturn(Map.of("type", "UNDO_SQL", "version", "V1"));

        FlywayAssert.assertThat(db).hasNotJustMigrated("V1");
    }

    @Test
    public void assertingLastNotMigratedShouldFailForDifferentVersion() {
        with2DistinctVersions((v1, v2) -> {
            when(db.queryForMap(QUERY_LAST_APPLIED_MIGRATION)).thenReturn(Map.of("type", "UNDO_SQL", "version", v1));

            assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> FlywayAssert.assertThat(db)
                    .hasNotJustMigrated(v2))
                    .withMessage(format("Expected last flyway migration to be '%s' version migration but was '%s'", v2, v1));
        });
    }

    @Test
    public void assertingLastNotMigratedShouldFailToAssertIfLastOperationWasUndo() {
        when(db.queryForMap(QUERY_LAST_APPLIED_MIGRATION)).thenReturn(Map.of("type", "SQL", "version", "ANY"));

        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> FlywayAssert.assertThat(db)
                .hasNotJustMigrated("anyVersion"))
                .withMessage("Expected last flyway migration type not to be 'SQL' but was 'SQL'");
    }

    private void with2DistinctVersions(final TwoVersionsConsumer consumer) {
        final val v1 = "V" + RandomStringUtils.randomNumeric(1);
        final val v2 = "V" + RandomStringUtils.randomNumeric(2);
        consumer.consume(v1, v2);
    }

    private static interface TwoVersionsConsumer {
        void consume(String first, String second);
    }
}
