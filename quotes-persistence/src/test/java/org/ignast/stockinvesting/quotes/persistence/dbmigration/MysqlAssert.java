package org.ignast.stockinvesting.quotes.persistence.dbmigration;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

public final class MysqlAssert extends AbstractAssert<MysqlAssert, JdbcTemplate> {

    private static JdbcTemplate database;

    MysqlAssert(final JdbcTemplate database) {
        super(database, MysqlAssert.class);
    }

    @SuppressWarnings("checkstyle:hiddenfield")
    static MysqlAssert assertThat(final JdbcTemplate database) {
        MysqlAssert.database = database;
        return new MysqlAssert(database);
    }

    public MysqlAssert containsTable(final String expectedTable) {
        final val result = database.queryForList("SHOW TABLES;");
        expectSingleColumnStartingWithTables(result);
        return expectResultsToContainTable(result, expectedTable);
    }

    public MysqlAssert notContainsTable(final String expectedTable) {
        final val result = database.queryForList("SHOW TABLES;");
        expectSingleColumnStartingWithTables(result);
        return expectResultsNotToContainTable(result, expectedTable);
    }

    private MysqlAssert expectResultsToContainTable(
        final List<Map<String, Object>> result,
        final String expectedTable
    ) {
        final val actualTables = result
            .stream()
            .flatMap(row -> row.values().stream())
            .collect(toUnmodifiableList());
        if (actualTables.contains(expectedTable)) {
            return null;
        } else {
            throw new AssertionError(
                format(
                    "List of tables in the database %s expected to contain '%s' table, but '%s' table did not exist",
                    actualTables,
                    expectedTable,
                    expectedTable
                )
            );
        }
    }

    private MysqlAssert expectResultsNotToContainTable(
        final List<Map<String, Object>> result,
        final String expectedTable
    ) {
        final val actualTables = result
            .stream()
            .flatMap(row -> row.values().stream())
            .collect(toUnmodifiableList());
        if (actualTables.contains(expectedTable)) {
            throw new AssertionError(
                format(
                    "List of tables in the database %s expected not to contain '%s' table, but '%s' table did exist",
                    actualTables,
                    expectedTable,
                    expectedTable
                )
            );
        }
        return null;
    }

    private void expectSingleColumnStartingWithTables(final List<Map<String, Object>> result) {
        if (!result.isEmpty()) {
            final val columns = result.get(0).keySet().stream().toList();
            if (columns.size() > 1) {
                throw new AssertionError(
                    format(
                        "Results from the database showing tables expected to have 1 column, but had multiple %s",
                        columns
                    )
                );
            } else if (columns.size() == 1 && !columns.get(0).startsWith("Tables")) {
                throw new AssertionError(
                    format(
                        "Results from the database showing tables expected to have a column starting with 'Tables', " +
                        "but was %s",
                        columns
                    )
                );
            }
        }
    }
}

final class MysqlAssertContainsTableTest {

    private final JdbcTemplate database = mock(JdbcTemplate.class);

    @Test
    public void shouldFailIfNoTableExistsInDatabase() {
        when(database.queryForList("SHOW TABLES;")).thenReturn(emptyList());

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).containsTable("client"))
            .withMessage(
                "List of tables in the database [] expected to contain 'client' table, but 'client' table did not exist"
            );
    }

    @Test
    public void shouldFailIfExpectedTableNotTheOnlyInDatabase() {
        when(database.queryForList("SHOW TABLES;"))
            .thenReturn(List.of(asMap("Tables_in_schema", "provider")));

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).containsTable("order"))
            .withMessage(
                "List of tables in the database [provider] expected to contain 'order' table, " +
                "but 'order' table did not exist"
            );
    }

    @Test
    public void shouldFailIfExpectedTableNotExistsInDatabase() {
        final val tablesNotContainingUser = asList(
            asMap("Tables_in_schema", "client"),
            asMap("Tables_in_schema", "provider"),
            asMap("Tables_in_schema", "order")
        );
        when(database.queryForList("SHOW TABLES;")).thenReturn(tablesNotContainingUser);

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).containsTable("user"))
            .withMessage(
                "List of tables in the database [client, provider, order] expected to contain 'user' table, " +
                "but 'user' table did not exist"
            );
    }

    @Test
    public void shouldFailIfResultsContainMoreThanOneColumn() {
        final val secondColumn = "another_column_" + RandomStringUtils.randomAlphabetic(3).toLowerCase();
        when(database.queryForList("SHOW TABLES;"))
            .thenReturn(aRecordInOrderedColumns("Tables_in_schema", "provider", secondColumn, "client"));

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).containsTable("order"))
            .withMessage(
                format(
                    "Results from the database showing tables expected to have 1 column, " +
                    "but had multiple [Tables_in_schema, %s]",
                    secondColumn
                )
            );
    }

    @Test
    public void shouldFailIfResultsDoNotContainColumnStartingWithLiteralTables() {
        when(database.queryForList("SHOW TABLES;"))
            .thenReturn(List.of(asMap("column_not_starting_with_tables", "any")));

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).containsTable("order"))
            .withMessage(
                "Results from the database showing tables expected to have a column starting with 'Tables', " +
                "but was [column_not_starting_with_tables]"
            );
    }

    private Map<String, List<Map<String, Object>>> tablesWithResultsContainingIt() {
        final val threeTables = asList(
            asMap("Tables_in_schema", "client"),
            asMap("Tables_in_schema", "order"),
            asMap("Tables_in_schema", "item")
        );
        return Map.of("client", threeTables, "order", threeTables, "item", threeTables);
    }

    @Test
    public void shouldAssertTableExists() {
        tablesWithResultsContainingIt()
            .forEach((table, result) -> {
                when(database.queryForList("SHOW TABLES;")).thenReturn(result);

                MysqlAssert.assertThat(database).containsTable(table);
            });
    }

    private List<Map<String, Object>> aRecordInOrderedColumns(
        final String column1Name,
        final String value1,
        final String column2Name,
        final String value2
    ) {
        final val dbRecord = new LinkedHashMap<String, Object>();
        dbRecord.put(column1Name, value1);
        dbRecord.put(column2Name, value2);
        return List.of(dbRecord);
    }

    private Map<String, Object> asMap(final String column, final String value) {
        return Map.of(column, value);
    }
}

class MysqlAssertNotContainsTableTest {

    private final JdbcTemplate database = mock(JdbcTemplate.class);

    @Test
    public void shouldSucceedIfNoTableExistsInDatabase() {
        when(database.queryForList("SHOW TABLES;")).thenReturn(emptyList());

        MysqlAssert.assertThat(database).notContainsTable("client");
    }

    @Test
    public void shouldSucceedIfExpectedTableIsNotTheOnlyInDatabase() {
        when(database.queryForList("SHOW TABLES;"))
            .thenReturn(List.of(asMap("Tables_in_schema", "provider")));

        MysqlAssert.assertThat(database).notContainsTable("order");
    }

    private Map<String, List<Map<String, Object>>> tableWithResultsNotContainingIt() {
        final val threeTables = asList(
            asMap("Tables_in_schema", "client"),
            asMap("Tables_in_schema", "provider"),
            asMap("Tables_in_schema", "order")
        );
        return Map.of("users", threeTables);
    }

    @Test
    public void shouldSucceedIfExpectedTableNotExistsInDatabase() {
        tableWithResultsNotContainingIt()
            .forEach((table, results) -> {
                when(database.queryForList("SHOW TABLES;")).thenReturn(results);

                MysqlAssert.assertThat(database).notContainsTable("user");
            });
    }

    @Test
    public void shouldFailIfResultsContainMoreThanOneColumn() {
        final val secondColumn = "another_column_" + RandomStringUtils.randomAlphabetic(3).toLowerCase();
        when(database.queryForList("SHOW TABLES;"))
            .thenReturn(aRecordInOrderedColumns("Tables_in_schema", "provider", secondColumn, "client"));

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).notContainsTable("order"))
            .withMessage(
                format(
                    "Results from the database showing tables expected to have 1 column, " +
                    "but had multiple [Tables_in_schema, %s]",
                    secondColumn
                )
            );
    }

    @Test
    public void shouldFailIfResultsDoNotContainColumnStartingWithLiteralTables() {
        when(database.queryForList("SHOW TABLES;"))
            .thenReturn(List.of(asMap("column_not_starting_with_tables", "any")));

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).notContainsTable("order"))
            .withMessage(
                "Results from the database showing tables expected to have a column starting with 'Tables', " +
                "but was [column_not_starting_with_tables]"
            );
    }

    private Map<String, List<Map<String, Object>>> tablesWithResultsContainingIt() {
        final val threeTables = asList(
            asMap("Tables_in_schema", "client"),
            asMap("Tables_in_schema", "order"),
            asMap("Tables_in_schema", "item")
        );
        return Map.of("client", threeTables, "order", threeTables, "item", threeTables);
    }

    @Test
    @SuppressWarnings("checkstyle:lambdabodylength")
    public void shouldFailIfTableExists() {
        tablesWithResultsContainingIt()
            .forEach((table, result) -> {
                when(database.queryForList("SHOW TABLES;")).thenReturn(result);

                assertThatExceptionOfType(AssertionError.class)
                    .isThrownBy(() -> MysqlAssert.assertThat(database).notContainsTable(table))
                    .withMessage(
                        format(
                            "List of tables in the database [client, order, item] " +
                            "expected not to contain '%s' table, but '%s' table did exist",
                            table,
                            table
                        )
                    );
            });
    }

    private List<Map<String, Object>> aRecordInOrderedColumns(
        final String column1Name,
        final String value1,
        final String column2Name,
        final String value2
    ) {
        final val dbRecord = new LinkedHashMap<String, Object>();
        dbRecord.put(column1Name, value1);
        dbRecord.put(column2Name, value2);
        return List.of(dbRecord);
    }

    private Map<String, Object> asMap(final String column, final String value) {
        return Map.of(column, value);
    }
}
