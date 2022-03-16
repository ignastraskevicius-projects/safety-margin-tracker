package org.ignast.stockinvesting.quotes.api.controller.integration.company;

import lombok.val;
import org.ignast.stockinvesting.quotes.CompanyName;
import org.ignast.stockinvesting.quotes.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.PositiveNumber;
import org.ignast.stockinvesting.quotes.StockSymbol;
import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.quotes.api.controller.DomainFactoryForTests.amazon;

public class CompanyJsonBodyFactory {
    public String createAmazon() {
        return "{\"id\":6,\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}";
    }

    public String createWithIdJsonPair(String jsonPair) {
        return format("{%s\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}", appendCommaIfNotEmpty(jsonPair));
    }

    public String createWithNameJsonPair(String nameJsonPair) {
        return format(
                "{\"id\":6,%s\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}",
                appendCommaIfNotEmpty(nameJsonPair));
    }

    public String createWithoutNameAndId() {
        return "{\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}";
    }

    public String createWithMultipleListings() {
        return createWithListingsJsonPair(
                "\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"},{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]");
    }

    public String createWithListingsJsonPair(String listingsJsonPair) {
        return format("{\"id\":6,\"name\":\"Amazon\"%s}",
                prependCommaIfNotEmpty(listingsJsonPair));
    }

    public String createWithMarketIdJsonPair(String marketIdJsonPair) {
        return format(
                "{\"id\":6,\"name\":\"Amazon\",\"listings\":[{%s\"stockSymbol\":\"AMZN\"}]}",
                appendCommaIfNotEmpty(marketIdJsonPair));
    }

    public String createWithSymbolJsonPair(String jsonPair) {
        return format(
                "{\"id\":6,\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNAS\"%s}]}",
                prependCommaIfNotEmpty(jsonPair));
    }

    private String appendCommaIfNotEmpty(String jsonPair) {
        return jsonPair + (jsonPair.isEmpty() ? "" : ",");
    }

    private String prependCommaIfNotEmpty(String jsonPair) {
        return (jsonPair.isEmpty() ? "" : ",") + jsonPair;
    }


}

class CompanyJsonBodyFactoryTest {

    private CompanyJsonBodyFactory factory = new CompanyJsonBodyFactory();

    @Test
    public void shouldCreateValidJsonRepresentingDomain() {
        val externalId = 6;
        val name = "Amazon";
        val mic = "XNAS";
        val symbol = "AMZN";
        assertThat(factory.createAmazon()).isEqualTo(format(
                "{\"id\":%d,\"name\":\"%s\",\"listings\":[{\"marketIdentifier\":\"%s\",\"stockSymbol\":\"%s\"}]}",
                externalId, name, mic, symbol));
        assertThat(amazon().getExternalId()).isEqualTo(new PositiveNumber(externalId));
        assertThat(amazon().getName()).isEqualTo(new CompanyName(name));
        assertThat(amazon().getStockSymbol()).isEqualTo(new StockSymbol(symbol));
        assertThat(amazon().getStockExchange().getMarketIdentifierCode()).isEqualTo(new MarketIdentifierCode(mic));
    }


    @Test
    public void shouldCreateCompanyWithoutId() {
        assertThat(factory.createWithIdJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomCustomId() {
        assertThat(factory.createWithIdJsonPair("\"id\":\"custom UUID\""))
                .isEqualTo(
                        "{\"id\":\"custom UUID\",\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutName() {
        assertThat(factory.createWithNameJsonPair("")).isEqualTo(
                "{\"id\":6,\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomNameJsonPair() {
        assertThat(factory.createWithNameJsonPair("\"name\":null")).isEqualTo(
                "{\"id\":6,\"name\":null,\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutListingsField() {
        assertThat(factory.createWithListingsJsonPair(""))
                .isEqualTo("{\"id\":6,\"name\":\"Amazon\"}");
    }

    @Test
    public void shouldCreateCompanyWithCustomListingsJsonPair() {
        assertThat(factory.createWithListingsJsonPair("\"listings\":null")).isEqualTo(
                "{\"id\":6,\"name\":\"Amazon\",\"listings\":null}");
    }

    @Test
    public void shouldCreateCompanyWithMultipleListings() {
        assertThat(factory.createWithMultipleListings()).isEqualTo(
                "{\"id\":6,\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"},{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithoutMarketIdField() {
        assertThat(factory.createWithMarketIdJsonPair("")).isEqualTo(
                "{\"id\":6,\"name\":\"Amazon\",\"listings\":[{\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithCustomMarketIdField() {
        assertThat(factory.createWithMarketIdJsonPair("\"marketIdentifier\":\"London Stock Exchange\"")).isEqualTo(
                "{\"id\":6,\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"London Stock Exchange\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithoutSymbolField() {
        assertThat(factory.createWithSymbolJsonPair("")).isEqualTo(
                "{\"id\":6,\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNAS\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithCustomSymbolField() {
        assertThat(factory.createWithSymbolJsonPair("\"stockSymbol\":\"Amazon\"")).isEqualTo(
                "{\"id\":6,\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateWithoutNameAndId() {
        assertThat(factory.createWithoutNameAndId()).isEqualTo(
                "{\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }
}
