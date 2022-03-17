package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class CompanyJsonBodyFactory {
    public String createAmazon() {
        return "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}";
    }

    public String createWithIdJsonPair(final String jsonPair) {
        return String.format("{%s\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}", appendCommaIfNotEmpty(jsonPair));
    }

    public String createWithNameJsonPair(final String nameJsonPair) {
        return String.format(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",%s\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}",
                appendCommaIfNotEmpty(nameJsonPair));
    }

    public String createWithoutNameAndCurrency() {
        return "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"homeCountry\":\"US\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}";
    }

    public String createWithHomeCountryJsonPair(final String homeCountryJsonPair) {
        return String.format(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",%s\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}",
                appendCommaIfNotEmpty(homeCountryJsonPair));
    }

    public String createWithMultipleListings() {
        return createWithListingsJsonPair(
                "\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"},{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]");
    }

    public String createWithListingsJsonPair(final String listingsJsonPair) {
        return String.format("{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\"%s}",
                prependCommaIfNotEmpty(listingsJsonPair));
    }

    public String createWithMarketIdJsonPair(final String marketIdJsonPair) {
        return String.format(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{%s\"stockSymbol\":\"AMZN\"}]}",
                appendCommaIfNotEmpty(marketIdJsonPair));
    }

    public String createWithSymbolJsonPair(final String jsonPair) {
        return String.format(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\"%s}]}",
                prependCommaIfNotEmpty(jsonPair));
    }

    public String createWithFunctionalCurrencyJsonPair(final String jsonPair) {
        return String.format(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",%s\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}",
                appendCommaIfNotEmpty(jsonPair));
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
    public void shouldCreateValidJson() {
        assertThat(factory.createAmazon()).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutName() {
        assertThat(factory.createWithNameJsonPair("")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomNameJsonPair() {
        assertThat(factory.createWithNameJsonPair("\"name\":null")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":null,\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutHomeCountry() {
        assertThat(factory.createWithHomeCountryJsonPair("")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomHomeCountryJsonPair() {
        assertThat(factory.createWithHomeCountryJsonPair("\"homeCountry\":null")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":null,\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutListingsField() {
        assertThat(factory.createWithListingsJsonPair(""))
                .isEqualTo("{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\"}");
    }

    @Test
    public void shouldCreateCompanyWithCustomListingsJsonPair() {
        assertThat(factory.createWithListingsJsonPair("\"listings\":null")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":null}");
    }

    @Test
    public void shouldCreateCompanyWithMultipleListings() {
        assertThat(factory.createWithMultipleListings()).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"},{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithoutMarketIdField() {
        assertThat(factory.createWithMarketIdJsonPair("")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithCustomMarketIdField() {
        assertThat(factory.createWithMarketIdJsonPair("\"marketIdentifier\":\"London Stock Exchange\"")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"London Stock Exchange\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithoutSymbolField() {
        assertThat(factory.createWithSymbolJsonPair("")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithCustomSymbolField() {
        assertThat(factory.createWithSymbolJsonPair("\"stockSymbol\":\"Amazon\"")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutFunctionalCurrency() {
        assertThat(factory.createWithFunctionalCurrencyJsonPair("")).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomFunctionalCurrency() {
        assertThat(factory.createWithFunctionalCurrencyJsonPair("\"functionalCurrency\":\"United Stated Dollar\""))
                .isEqualTo(
                        "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"United Stated Dollar\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutId() {
        assertThat(factory.createWithIdJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomCustomId() {
        assertThat(factory.createWithIdJsonPair("\"id\":\"custom UUID\""))
                .isEqualTo(
                        "{\"id\":\"custom UUID\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }

    @Test
    public void shouldCreateWithoutNameAndCurrency() {
        assertThat(factory.createWithoutNameAndCurrency()).isEqualTo(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"homeCountry\":\"US\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}");
    }
}
