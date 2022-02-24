package org.ignast.stockinvesting.api.controller;

public class CompanyJsonBodyFactory {
    public String createAmazon() {
        return "{\"name\":\"Amazon\",\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}";
    }

    public String createWithNameJsonPair(String nameJsonPair) {
        return String.format(
                "{%s\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}",
                appendCommaIfNotEmpty(nameJsonPair));
    }

    public String createWithoutNameAndCurrency() {
        return "{\"homeCountry\":\"United States\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}";
    }

    public String createWithHomeCountryJsonPair(String homeCountryJsonPair) {
        return String.format(
                "{\"name\":\"Amazon\",%s\"functionalCurrency\":\"USD\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}",
                appendCommaIfNotEmpty(homeCountryJsonPair));
    }

    public String createWithListingsJsonPair(String listingsJsonPair) {
        return String.format("{\"name\":\"Amazon\",\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\"%s}",
                prependCommaIfNotEmpty(listingsJsonPair));
    }

    public String createWithStockExchangeJsonPair(String stockExchangeJsonPair) {
        return String.format(
                "{\"name\":\"Amazon\",\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\",\"listings\":[{%s\"ticker\":\"Amazon\"}]}",
                appendCommaIfNotEmpty(stockExchangeJsonPair));
    }

    public String createWithTickerJsonPair(String jsonPair) {
        return String.format(
                "{\"name\":\"Amazon\",\"homeCountry\":\"United States\",\"functionalCurrency\":\"USD\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\"%s}]}",
                prependCommaIfNotEmpty(jsonPair));
    }

    public String createWithFunctionalCurrencyJsonPair(String jsonPair) {
        return String.format(
                "{\"name\":\"Amazon\",\"homeCountry\":\"United States\",%s\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}",
                appendCommaIfNotEmpty(jsonPair));
    }

    private String appendCommaIfNotEmpty(String jsonPair) {
        return jsonPair + (jsonPair.isEmpty() ? "" : ",");
    }

    private String prependCommaIfNotEmpty(String jsonPair) {
        return (jsonPair.isEmpty() ? "" : ",") + jsonPair;
    }
}
