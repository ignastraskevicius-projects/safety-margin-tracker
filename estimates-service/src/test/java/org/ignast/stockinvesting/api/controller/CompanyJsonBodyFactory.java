package org.ignast.stockinvesting.api.controller;

public class CompanyJsonBodyFactory {
    public String createAmazon() {
        return "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}";
    }

    public String createWithIdJsonPair(String jsonPair) {
        return String.format("{%s\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}", appendCommaIfNotEmpty(jsonPair));
    }

    public String createWithNameJsonPair(String nameJsonPair) {
        return String.format(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",%s\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}",
                appendCommaIfNotEmpty(nameJsonPair));
    }

    public String createWithoutNameAndCurrency() {
        return "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"homeCountry\":\"US\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}";
    }

    public String createWithHomeCountryJsonPair(String homeCountryJsonPair) {
        return String.format(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",%s\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}",
                appendCommaIfNotEmpty(homeCountryJsonPair));
    }

    public String createWithMultipleListings() {
        return createWithListingsJsonPair(
                "\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"},{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]");
    }

    public String createWithListingsJsonPair(String listingsJsonPair) {
        return String.format("{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\"%s}",
                prependCommaIfNotEmpty(listingsJsonPair));
    }

    public String createWithMarketIdJsonPair(String marketIdJsonPair) {
        return String.format(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{%s\"stockSymbol\":\"AMZN\"}]}",
                appendCommaIfNotEmpty(marketIdJsonPair));
    }

    public String createWithSymbolJsonPair(String jsonPair) {
        return String.format(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\"%s}]}",
                prependCommaIfNotEmpty(jsonPair));
    }

    public String createWithFunctionalCurrencyJsonPair(String jsonPair) {
        return String.format(
                "{\"id\":\"339d7d9e-d837-47bd-971b-d52e965e6968\",\"name\":\"Amazon\",\"homeCountry\":\"US\",%s\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}",
                appendCommaIfNotEmpty(jsonPair));
    }

    private String appendCommaIfNotEmpty(String jsonPair) {
        return jsonPair + (jsonPair.isEmpty() ? "" : ",");
    }

    private String prependCommaIfNotEmpty(String jsonPair) {
        return (jsonPair.isEmpty() ? "" : ",") + jsonPair;
    }


}
