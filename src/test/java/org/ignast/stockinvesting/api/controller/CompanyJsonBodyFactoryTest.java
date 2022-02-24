package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CompanyJsonBodyFactoryTest {

    private CompanyJsonBodyFactory factory = new CompanyJsonBodyFactory();

    @Test
    public void shouldCreateValidJson() {
        assertThat(factory.createAmazon()).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutName() {
        assertThat(factory.createWithNameJsonPair("")).isEqualTo(
                "{\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomNameJsonPair() {
        assertThat(factory.createWithNameJsonPair("\"name\":null")).isEqualTo(
                "{\"name\":null,\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutHomeCountry() {
        assertThat(factory.createWithHomeCountryJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"functionalCurrency\":\"USD\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomHomeCountryJsonPair() {
        assertThat(factory.createWithHomeCountryJsonPair("\"homeCountry\":null")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":null,\"functionalCurrency\":\"USD\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutListingsField() {
        assertThat(factory.createWithListingsJsonPair(""))
                .isEqualTo("{\"name\":\"Amazon\",\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\"}");
    }

    @Test
    public void shouldCreateCompanyWithCustomListingsJsonPair() {
        assertThat(factory.createWithListingsJsonPair("\"listings\":null")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\",\"listings\":null}");
    }

    @Test
    public void shouldCreateListedCompanyWithoutStockExchangeField() {
        assertThat(factory.createWithStockExchangeJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\",\"listings\":[{\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithCustomStockExchangeField() {
        assertThat(factory.createWithStockExchangeJsonPair("\"stockExchange\":\"London Stock Exchange\"")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\",\"listings\":[{\"stockExchange\":\"London Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithoutTickerField() {
        assertThat(factory.createWithTickerJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithCustomTickerField() {
        assertThat(factory.createWithTickerJsonPair("\"ticker\":\"Amazon\"")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutFunctionalCurrency() {
        assertThat(factory.createWithFunctionalCurrencyJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"homeCountry\":\"United States\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomFunctionalCurrency() {
        assertThat(factory.createWithFunctionalCurrencyJsonPair("\"functionalCurrency\":\"United Stated Dollar\""))
                .isEqualTo(
                        "{\"name\":\"Amazon\",\"homeCountry\":\"United States\",\"functionalCurrency\":\"United Stated Dollar\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateWithoutNameAndCurrency() {
        assertThat(factory.createWithoutNameAndCurrency()).isEqualTo(
                "{\"homeCountry\":\"United States\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }
}
