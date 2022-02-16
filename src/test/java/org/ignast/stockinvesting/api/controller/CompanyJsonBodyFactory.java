package org.ignast.stockinvesting.api.controller;

public class CompanyJsonBodyFactory {
    public String createAmazon() {
        return "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"listings\":\"listings\"}";
    }

    public String createWithNameJsonPair(String nameJsonPair) {
        return String.format("{%s\"address\":{\"country\":\"United States\"},\"listings\":\"listings\"}",
                appendCommaIfNotEmpty(nameJsonPair));
    }

    public String createWithAddressJsonPair(String addressJsonPair) {
        return String.format("{\"name\":\"Amazon\",%s\"listings\":\"listings\"}",
                appendCommaIfNotEmpty(addressJsonPair));
    }

    private String appendCommaIfNotEmpty(String jsonPair) {
        return jsonPair + (jsonPair.isEmpty() ? "" : ",");
    }

    public String createWithCountryJsonPair(String countryJsonPair) {
        return String.format("{\"name\":\"Amazon\",\"address\":{%s},\"listings\":\"listings\"}", countryJsonPair);
    }
}
