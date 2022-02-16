package org.ignast.stockinvesting.api.controller;

public class CompanyJsonBodyFactory {
    public String createAmazon() {
        return "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"listings\":[{}]}";
    }

    public String createWithNameJsonPair(String nameJsonPair) {
        return String.format("{%s\"address\":{\"country\":\"United States\"},\"listings\":[{}]}",
                appendCommaIfNotEmpty(nameJsonPair));
    }

    public String createWithAddressJsonPair(String addressJsonPair) {
        return String.format("{\"name\":\"Amazon\",%s\"listings\":[{}]}", appendCommaIfNotEmpty(addressJsonPair));
    }

    public String createWithCountryJsonPair(String countryJsonPair) {
        return String.format("{\"name\":\"Amazon\",\"address\":{%s},\"listings\":[{}]}", countryJsonPair);
    }

    public String createWithListingsJsonPair(String listingsJsonPair) {
        return String.format("{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"}%s}",
                prependCommaIfNotEmpty(listingsJsonPair));
    }

    private String appendCommaIfNotEmpty(String jsonPair) {
        return jsonPair + (jsonPair.isEmpty() ? "" : ",");
    }

    private String prependCommaIfNotEmpty(String jsonPair) {
        return (jsonPair.isEmpty() ? "" : ",") + jsonPair;
    }
}
