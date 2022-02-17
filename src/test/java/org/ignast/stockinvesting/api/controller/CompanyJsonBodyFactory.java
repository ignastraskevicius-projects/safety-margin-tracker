package org.ignast.stockinvesting.api.controller;

public class CompanyJsonBodyFactory {
    public String createAmazon() {
        return "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"abc\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}";
    }

    public String createWithNameJsonPair(String nameJsonPair) {
        return String.format(
                "{%s\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"abc\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}",
                appendCommaIfNotEmpty(nameJsonPair));
    }

    public String createWithAddressJsonPair(String addressJsonPair) {
        return String.format(
                "{\"name\":\"Amazon\",%s\"functionalCurrency\":\"abc\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}",
                appendCommaIfNotEmpty(addressJsonPair));
    }

    public String createWithCountryJsonPair(String countryJsonPair) {
        return String.format(
                "{\"name\":\"Amazon\",\"address\":{%s},\"functionalCurrency\":\"abc\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}",
                countryJsonPair);
    }

    public String createWithListingsJsonPair(String listingsJsonPair) {
        return String.format(
                "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"abc\"%s}",
                prependCommaIfNotEmpty(listingsJsonPair));
    }

    public String createWithStockExchangeJsonPair(String stockExchangeJsonPair) {
        return String.format(
                "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"abc\",\"listings\":[{%s\"ticker\":\"Amazon\"}]}",
                appendCommaIfNotEmpty(stockExchangeJsonPair));
    }

    public String createWithTickerJsonPair(String jsonPair) {
        return String.format(
                "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"functionalCurrency\":\"abc\",\"listings\":[{\"stockExchange\":\"New York Stock Exchange\"%s}]}",
                prependCommaIfNotEmpty(jsonPair));
    }

    public String createWithFunctionalCurrencyJsonPair(String jsonPair) {
        return String.format(
                "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},%s\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}]}",
                appendCommaIfNotEmpty(jsonPair));
    }

    private String appendCommaIfNotEmpty(String jsonPair) {
        return jsonPair + (jsonPair.isEmpty() ? "" : ",");
    }

    private String prependCommaIfNotEmpty(String jsonPair) {
        return (jsonPair.isEmpty() ? "" : ",") + jsonPair;
    }

}
