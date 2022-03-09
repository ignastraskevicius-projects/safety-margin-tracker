package org.ignast.stockinvesting.quotes.api.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CompanyJsonBodyFactory {
    public String createAmazon() {
        return "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}";
    }

    public String createWithIdJsonPair(String jsonPair) {
        return String.format("{%s\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}", appendCommaIfNotEmpty(jsonPair));
    }

    public String createWithNameJsonPair(String nameJsonPair) {
        return String.format(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",%s\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}",
                appendCommaIfNotEmpty(nameJsonPair));
    }

    public String createWithoutNameAndId() {
        return "{\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}";
    }

    public String createWithMultipleListings() {
        return createWithListingsJsonPair(
                "\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"},{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]");
    }

    public String createWithListingsJsonPair(String listingsJsonPair) {
        return String.format("{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\"%s}",
                prependCommaIfNotEmpty(listingsJsonPair));
    }

    public String createWithMarketIdJsonPair(String marketIdJsonPair) {
        return String.format(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"listings\":[{%s\"stockSymbol\":\"AMZN\"}]}",
                appendCommaIfNotEmpty(marketIdJsonPair));
    }

    public String createWithSymbolJsonPair(String jsonPair) {
        return String.format(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNYS\"%s}]}",
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
    public void shouldCreateValidJson() {
        assertThat(factory.createAmazon()).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}");
    }


    @Test
    public void shouldCreateCompanyWithoutId() {
        assertThat(factory.createWithIdJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomCustomId() {
        assertThat(factory.createWithIdJsonPair("\"id\":\"custom UUID\""))
                .isEqualTo(
                        "{\"id\":\"custom UUID\",\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutName() {
        assertThat(factory.createWithNameJsonPair("")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomNameJsonPair() {
        assertThat(factory.createWithNameJsonPair("\"name\":null")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":null,\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutListingsField() {
        assertThat(factory.createWithListingsJsonPair(""))
                .isEqualTo("{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\"}");
    }

    @Test
    public void shouldCreateCompanyWithCustomListingsJsonPair() {
        assertThat(factory.createWithListingsJsonPair("\"listings\":null")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"listings\":null}");
    }

    @Test
    public void shouldCreateCompanyWithMultipleListings() {
        assertThat(factory.createWithMultipleListings()).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"},{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithoutMarketIdField() {
        assertThat(factory.createWithMarketIdJsonPair("")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"listings\":[{\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithCustomMarketIdField() {
        assertThat(factory.createWithMarketIdJsonPair("\"marketIdentifier\":\"London Stock Exchange\"")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"London Stock Exchange\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithoutSymbolField() {
        assertThat(factory.createWithSymbolJsonPair("")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNYS\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithCustomSymbolField() {
        assertThat(factory.createWithSymbolJsonPair("\"stockSymbol\":\"Amazon\"")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateWithoutNameAndId() {
        assertThat(factory.createWithoutNameAndId()).isEqualTo(
                "{\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}");
    }
}
