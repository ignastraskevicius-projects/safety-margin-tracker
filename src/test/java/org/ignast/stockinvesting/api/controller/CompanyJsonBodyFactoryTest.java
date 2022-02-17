package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CompanyJsonBodyFactoryTest {

    private CompanyJsonBodyFactory factory = new CompanyJsonBodyFactory();

    @Test
    public void shouldCreateValidJson() {
        assertThat(factory.createAmazon()).isEqualTo(
                "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"ABC\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutName() {
        assertThat(factory.createWithNameJsonPair("")).isEqualTo(
                "{\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"ABC\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomNameJsonPair() {
        assertThat(factory.createWithNameJsonPair("\"name\":null")).isEqualTo(
                "{\"name\":null,\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"ABC\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutAddress() {
        assertThat(factory.createWithAddressJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"functionalCurrency\":\"ABC\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomAddressJsonPair() {
        assertThat(factory.createWithAddressJsonPair("\"address\":null")).isEqualTo(
                "{\"name\":\"Amazon\",\"address\":null,\"functionalCurrency\":\"ABC\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutCountry() {
        assertThat(factory.createWithCountryJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"address\":{},\"functionalCurrency\":\"abc\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomCountryJsonPair() {
        assertThat(factory.createWithCountryJsonPair("\"country\":null")).isEqualTo(
                "{\"name\":\"Amazon\",\"address\":{\"country\":null},\"functionalCurrency\":\"abc\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutListingsField() {
        assertThat(factory.createWithListingsJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"abc\"}");
    }

    @Test
    public void shouldCreateCompanyWithCustomListingsJsonPair() {
        assertThat(factory.createWithListingsJsonPair("\"listings\":null")).isEqualTo(
                "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"abc\",\"listings\":null}");
    }

    @Test
    public void shouldCreateListedCompanyWithoutStockExchangeField() {
        assertThat(factory.createWithStockExchangeJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"abc\",\"listings\":[{\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithCustomStockExchangeField() {
        assertThat(factory.createWithStockExchangeJsonPair("\"stockExchange\":\"London Stock Exchange\"")).isEqualTo(
                "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"abc\",\"listings\":[{\"stockExchange\":\"London Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithoutTickerField() {
        assertThat(factory.createWithTickerJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"abc\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\"}]}");
    }

    @Test
    public void shouldCreateListedCompanyWithCustomTickerField() {
        assertThat(factory.createWithTickerJsonPair("\"ticker\":\"Amazon\"")).isEqualTo(
                "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"abc\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithoutFunctionalCurrency() {
        assertThat(factory.createWithFunctionalCurrencyJsonPair("")).isEqualTo(
                "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }

    @Test
    public void shouldCreateCompanyWithCustomFunctionalCurrency() {
        assertThat(factory.createWithFunctionalCurrencyJsonPair("\"functionalCurrency\":\"United Stated Dollar\""))
                .isEqualTo(
                        "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"United Stated Dollar\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}");
    }
}
