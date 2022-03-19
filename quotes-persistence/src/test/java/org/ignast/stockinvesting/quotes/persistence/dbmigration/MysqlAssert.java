package org.ignast.stockinvesting.quotes.persistence.dbmigration;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.assertj.core.api.AbstractAssert;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

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

    public void containsTable(final String expectedTableName) {
        final List<Object> listedTables = getListedTables();
        assertContainsTable(expectedTableName, listedTables);
    }

    public void notContainsTable(final String expectedTableName) {
        final List<Object> listedTables = getListedTables();
        assertNotContainsTable(expectedTableName, listedTables);
    }

    private List<Object> getListedTables() {
        final val rows = database.queryForList("SHOW TABLES;");
        final val columnNameForTables = getColumnNameForTables(rows);
        final val listedTables = rows
            .stream()
            .map(r -> r.get(columnNameForTables))
            .collect(toUnmodifiableList());
        return listedTables;
    }

    private void assertContainsTable(final String expectedTableName, final List<Object> tables) {
        if (!tables.contains(expectedTableName)) {
            throw errorTableNotFound(expectedTableName, tables);
        }
    }

    private void assertNotContainsTable(final String expectedTableName, final List<Object> tables) {
        if (tables.contains(expectedTableName)) {
            throw errorTableExists(expectedTableName, tables);
        }
    }

    private String getColumnNameForTables(final List<Map<String, Object>> rows) {
        return rows.stream().findFirst().map(row -> extractColumnNameForTablesFrom(row)).orElse("Tables");
    }

    @NotNull
    private String extractColumnNameForTablesFrom(final Map<String, Object> row) {
        final val columnNames = row.keySet().stream().toList();
        final String theOnlyColumnName = getTheOnlyColumnName(columnNames);
        if (!theOnlyColumnName.startsWith("Tables")) {
            throw errorNoTablesColumn(columnNames);
        }
        return theOnlyColumnName;
    }

    private String getTheOnlyColumnName(final List<String> columnNames) {
        assertThatHasOnlyOneColumnName(columnNames);
        final val theOnlyColumn = columnNames
            .stream()
            .findFirst()
            .orElseThrow(() -> errorNoColumnsReturned());
        return theOnlyColumn;
    }

    private void assertThatHasOnlyOneColumnName(final List<String> columnNames) {
        if (columnNames.size() > 1) {
            throw errorMultipleColumns(columnNames);
        }
        if (columnNames.isEmpty()) {
            errorNoColumnsReturned();
        }
    }

    private AssertionError errorNoColumnsReturned() {
        return new AssertionError("Broken database query result: result contains no columns");
    }

    private AssertionError errorTableNotFound(final String expectedTable, final List<Object> actualTables) {
        return new AssertionError(
            format(
                "List of tables in the database %s expected to contain '%s' table, " +
                "but '%s' table did not exist",
                actualTables,
                expectedTable,
                expectedTable
            )
        );
    }

    private AssertionError errorTableExists(final String expectedTable, final List<Object> actualTables) {
        return new AssertionError(
            format(
                "List of tables in the database %s expected not to contain '%s' table, " +
                "but '%s' table did exist",
                actualTables,
                expectedTable,
                expectedTable
            )
        );
    }

    private AssertionError errorNoTablesColumn(final List<String> columns) {
        return new AssertionError(
            format(
                "Results from the database showing tables expected to have a column " +
                "starting with 'Tables', but was %s",
                columns
            )
        );
    }

    private AssertionError errorMultipleColumns(final List<String> columns) {
        return new AssertionError(
            format(
                "Results from the database showing tables expected to have 1 column, " +
                "but had multiple %s",
                columns
            )
        );
    }
}

final class MysqlAssertContainsTableTest {

    private final JdbcTemplate database = mock(JdbcTemplate.class);

    @Test
    public void shouldFailIfNoTableExistsInDatabase() {
        when(database.queryForList("SHOW TABLES;")).thenReturn(Collections.emptyList());

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).containsTable("client"))
            .withMessage(
                "List of tables in the database [] expected to contain 'client' table, " +
                "but 'client' table did not exist"
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
    public void shouldFailIfQueryingDatabaseResultsInNoColumnsReturned() {
        when(database.queryForList("SHOW TABLES;")).thenReturn(List.of(Collections.emptyMap()));

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).containsTable("order"))
            .withMessage("Broken database query result: result contains no columns");
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
                "List of tables in the database [client, provider, order] " +
                "expected to contain 'user' table, but 'user' table did not exist"
            );
    }

    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldFailIfResultsContainMoreThanOneColumn() {
        when(database.queryForList("SHOW TABLES;"))
            .thenReturn(aRecordInOrderedColumns("Tables_in_schema", "provider", "another_column", "client"));

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).containsTable("order"))
            .withMessage(
                "Results from the database showing tables expected to have 1 column, " +
                "but had multiple [Tables_in_schema, another_column]"
            );
    }

    @Test
    public void shouldFailIfResultsDoNotContainColumnStartingWithLiteralTables() {
        when(database.queryForList("SHOW TABLES;"))
            .thenReturn(List.of(Map.of("column_not_starting_with_tables", "any")));

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).containsTable("order"))
            .withMessage(
                "Results from the database showing tables expected to have a column " +
                "starting with 'Tables', but was [column_not_starting_with_tables]"
            );
    }

    private Map<String, List<Map<String, Object>>> tablesWithResultsContainingIt() {
        final List<Map<String, Object>> threeTables = asList(
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

final class MysqlAssertNotContainsTableTest {

    private final JdbcTemplate database = mock(JdbcTemplate.class);

    @Test
    public void shouldSucceedIfNoTableExistsInDatabase() {
        when(database.queryForList("SHOW TABLES;")).thenReturn(Collections.emptyList());

        MysqlAssert.assertThat(database).notContainsTable("client");
    }

    @Test
    public void shouldSucceedIfExpectedTableIsNotTheOnlyInDatabase() {
        when(database.queryForList("SHOW TABLES;"))
            .thenReturn(List.of(asMap("Tables_in_schema", "provider")));

        MysqlAssert.assertThat(database).notContainsTable("order");
    }

    @Test
    public void shouldFailIfQueryingDatabaseResultsInNoColumnsReturned() {
        when(database.queryForList("SHOW TABLES;")).thenReturn(List.of(Collections.emptyMap()));

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).notContainsTable("order"))
            .withMessage("Broken database query result: result contains no columns");
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
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldFailIfResultsContainMoreThanOneColumn() {
        when(database.queryForList("SHOW TABLES;"))
            .thenReturn(aRecordInOrderedColumns("Tables_in_schema", "provider", "another_column", "client"));

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).notContainsTable("order"))
            .withMessage(
                "Results from the database showing tables expected to have 1 column, " +
                "but had multiple [Tables_in_schema, another_column]"
            );
    }

    @Test
    public void shouldFailIfResultsDoNotContainColumnStartingWithLiteralTables() {
        when(database.queryForList("SHOW TABLES;"))
            .thenReturn(List.of(asMap("column_not_starting_with_tables", "any")));

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> MysqlAssert.assertThat(database).notContainsTable("order"))
            .withMessage(
                "Results from the database showing tables expected to have a column " +
                "starting with 'Tables', but was [column_not_starting_with_tables]"
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
