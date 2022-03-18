package org.ignast.stockinvesting.quotes.api.controller.integration.company;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.quotes.api.testutil.DomainFactoryForTests.amazon;
import static org.ignast.stockinvesting.testutil.api.JsonAssert.assertThatJson;

import lombok.val;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

public final class CompanyJsonBodyFactory {

    public String createAmazon() {
        return """
                {
                    "id":6,
                    "name":"Amazon",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }""";
    }

    public String createWithIdJsonPair(final String jsonPair) {
        return format(
            """
                {
                    %s
                    "name":"Amazon",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }""",
            appendCommaIfNotEmpty(jsonPair)
        );
    }

    public String createWithNameJsonPair(final String nameJsonPair) {
        return format(
            """
                        {
                            "id":6,
                            %s
                            "listings":[{
                                "marketIdentifier":"XNAS",
                                "stockSymbol":"AMZN"
                            }]
                        }""",
            appendCommaIfNotEmpty(nameJsonPair)
        );
    }

    public String createWithoutNameAndId() {
        return """
                {
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }""";
    }

    public String createWithMultipleListings() {
        return createWithListingsJsonPair(
            """
            "listings":[{
                "marketIdentifier":"XNAS",
                "stockSymbol":"AMZN"
            },{
                "marketIdentifier":"XNAS",
                "stockSymbol":"AMZN"
            }]"""
        );
    }

    public String createWithListingsJsonPair(final String listingsJsonPair) {
        return format(
            """
                        {
                            "id":6,"name":"Amazon"
                            %s
                        }
                        """,
            prependCommaIfNotEmpty(listingsJsonPair)
        );
    }

    public String createWithMarketIdJsonPair(final String marketIdJsonPair) {
        return format(
            """
                        {
                            "id":6,
                            "name":"Amazon",
                            "listings":[{
                                %s
                                "stockSymbol":"AMZN"
                            }]
                        }""",
            appendCommaIfNotEmpty(marketIdJsonPair)
        );
    }

    public String createWithSymbolJsonPair(final String jsonPair) {
        return format(
            """
                        {
                            "id":6,
                            "name":"Amazon",
                            "listings":[{
                                "marketIdentifier":"XNAS"
                                %s
                            }]
                        }""",
            prependCommaIfNotEmpty(jsonPair)
        );
    }

    private String appendCommaIfNotEmpty(final String jsonPair) {
        if (jsonPair.isEmpty()) {
            return "";
        } else {
            return jsonPair + ",";
        }
    }

    private String prependCommaIfNotEmpty(final String jsonPair) {
        if (jsonPair.isEmpty()) {
            return "";
        } else {
            return "," + jsonPair;
        }
    }
}

final class CompanyJsonBodyFactoryTest {

    private final CompanyJsonBodyFactory factory = new CompanyJsonBodyFactory();

    @Test
    public void shouldCreateValidJsonRepresentingDomain() {
        final val externalId = 6;
        final val name = "Amazon";
        final val mic = "XNAS";
        final val symbol = "AMZN";
        assertThat(factory.createAmazon())
            .isEqualTo(
                format(
                    """
                        {
                            "id":%d,
                            "name":"%s",
                            "listings":[{
                                "marketIdentifier":"%s",
                                "stockSymbol":"%s"
                            }]
                        }""",
                    externalId,
                    name,
                    mic,
                    symbol
                )
            );
        assertThat(amazon().getExternalId()).isEqualTo(new CompanyExternalId(externalId));
        assertThat(amazon().getName()).isEqualTo(new CompanyName(name));
        assertThat(amazon().getStockSymbol()).isEqualTo(new StockSymbol(symbol));
        assertThat(amazon().getStockExchange().getMarketIdentifierCode())
            .isEqualTo(new MarketIdentifierCode(mic));
    }

    @Test
    public void shouldCreateCompanyWithoutId() throws JSONException {
        assertThatJson(factory.createWithIdJsonPair(""))
            .isEqualTo(
                """
                {
                    "name":"Amazon",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithCustomCustomId() throws JSONException {
        assertThatJson(factory.createWithIdJsonPair("\"id\":\"custom id\""))
            .isEqualTo(
                """
                {
                    "id":"custom id",
                    "name":"Amazon",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithoutName() throws JSONException {
        assertThatJson(factory.createWithNameJsonPair(""))
            .isEqualTo(
                """
                {
                    "id":6,
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithCustomNameJsonPair() throws JSONException {
        assertThatJson(factory.createWithNameJsonPair("\"name\":null"))
            .isEqualTo(
                """
                {
                    "id":6,
                    "name":null,
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithoutListingsField() throws JSONException {
        assertThatJson(factory.createWithListingsJsonPair(""))
            .isEqualTo(
                """
                        {
                            "id":6,
                            "name":"Amazon"
                        }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithCustomListingsJsonPair() throws JSONException {
        assertThatJson(factory.createWithListingsJsonPair("\"listings\":null"))
            .isEqualTo(
                """
                {
                    "id":6,
                    "name":"Amazon",
                    "listings":null
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithMultipleListings() throws JSONException {
        assertThatJson(factory.createWithMultipleListings())
            .isEqualTo(
                """
                {
                    "id":6,
                    "name":"Amazon",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    },{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateListedCompanyWithoutMarketIdField() throws JSONException {
        assertThatJson(factory.createWithMarketIdJsonPair(""))
            .isEqualTo(
                """
                {
                    "id":6,
                    "name":"Amazon",
                    "listings":[{
                        "stockSymbol":"AMZN"
                    }]}"""
            );
    }

    @Test
    public void shouldCreateListedCompanyWithCustomMarketIdField() throws JSONException {
        assertThatJson(factory.createWithMarketIdJsonPair("\"marketIdentifier\":\"London Stock Exchange\""))
            .isEqualTo(
                """
                {
                    "id":6,
                    "name":"Amazon",
                    "listings":[{
                        "marketIdentifier":"London Stock Exchange",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateListedCompanyWithoutSymbolField() throws JSONException {
        assertThatJson(factory.createWithSymbolJsonPair(""))
            .isEqualTo(
                """
                {
                    "id":6,
                    "name":"Amazon",
                    "listings":[{
                        "marketIdentifier":"XNAS"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateListedCompanyWithCustomSymbolField() throws JSONException {
        assertThatJson(factory.createWithSymbolJsonPair("\"stockSymbol\":\"Amazon\""))
            .isEqualTo(
                """
                {
                    "id":6,
                    "name":"Amazon",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"Amazon"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateWithoutNameAndId() throws JSONException {
        assertThatJson(factory.createWithoutNameAndId())
            .isEqualTo(
                """
                {
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }
}
