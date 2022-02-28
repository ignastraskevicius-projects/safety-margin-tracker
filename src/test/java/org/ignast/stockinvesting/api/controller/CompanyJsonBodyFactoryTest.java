package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CompanyJsonBodyFactoryTest {

    private CompanyJsonBodyFactory factory = new CompanyJsonBodyFactory();

    @Test
    public void shouldCreateValidJson() {
        assertThat(factory.createAmazon()).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutName() {
        assertThat(factory.createWithNameJsonPair("")).isEqualTo(
                "{\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomNameJsonPair() {
        assertThat(factory.createWithNameJsonPair("\"name\":null")).isEqualTo(
                "{\"name\":null,\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutHomeCountry() {
        assertThat(factory.createWithHomeCountryJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomHomeCountryJsonPair() {
        assertThat(factory.createWithHomeCountryJsonPair("\"homeCountry\":null")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":null,\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutListingsField() {
        assertThat(factory.createWithListingsJsonPair(""))
                .isEqualTo("{\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\"}");
    }

    @Test
    public void shouldCreateCompanyWithCustomListingsJsonPair() {
        assertThat(factory.createWithListingsJsonPair("\"listings\":null")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":null}");
    }

    @Test
    public void shouldCreateListedCompanyWithoutMarketIdField() {
        assertThat(factory.createWithMarketIdJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"stockSymbol\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithCustomMarketIdField() {
        assertThat(factory.createWithMarketIdJsonPair("\"marketIdentifier\":\"London Stock Exchange\"")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"London Stock Exchange\",\"stockSymbol\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithoutSymbolField() {
        assertThat(factory.createWithSymbolJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithCustomSymbolField() {
        assertThat(factory.createWithSymbolJsonPair("\"stockSymbol\":\"Amazon\"")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutFunctionalCurrency() {
        assertThat(factory.createWithFunctionalCurrencyJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"US\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomFunctionalCurrency() {
        assertThat(factory.createWithFunctionalCurrencyJsonPair("\"functionalCurrency\":\"United Stated Dollar\""))
                .isEqualTo(
                        "{\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"United Stated Dollar\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateWithoutNameAndCurrency() {
        assertThat(factory.createWithoutNameAndCurrency()).isEqualTo(
                "{\"homeCountry\":\"US\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"Amazon\"}]}");
    }
}
