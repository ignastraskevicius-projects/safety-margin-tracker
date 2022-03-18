package org.ignast.stockinvesting.api.controller;

import static org.ignast.stockinvesting.testutil.api.JsonAssert.assertThatJson;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

public final class CompanyJsonBodyFactory {

    public String createAmazon() {
        return """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "name":"Amazon",
                    "homeCountry":"US",
                    "functionalCurrency":"USD",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }""";
    }

    public String createWithIdJsonPair(final String jsonPair) {
        return String.format(
            """
                {
                    %s
                    "name":"Amazon",
                    "homeCountry":"US",
                    "functionalCurrency":"USD",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }""",
            appendCommaIfNotEmpty(jsonPair)
        );
    }

    public String createWithNameJsonPair(final String nameJsonPair) {
        return String.format(
            """
                        {
                            "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                            %s
                            "homeCountry":"US",
                            "functionalCurrency":"USD",
                            "listings":[{
                                "marketIdentifier":"XNAS",
                                "stockSymbol":"AMZN"
                            }]
                        }""",
            appendCommaIfNotEmpty(nameJsonPair)
        );
    }

    public String createWithoutNameAndCurrency() {
        return """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "homeCountry":"US",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }""";
    }

    public String createWithHomeCountryJsonPair(final String homeCountryJsonPair) {
        return String.format(
            """
                        {
                            "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                            "name":"Amazon",
                            %s
                            "functionalCurrency":"USD",
                            "listings":[{
                                "marketIdentifier":"XNAS",
                                "stockSymbol":"AMZN"
                            }]
                        }""",
            appendCommaIfNotEmpty(homeCountryJsonPair)
        );
    }

    public String createWithMultipleListings() {
        return createWithListingsJsonPair(
            """
                        "listings":[{
                            "marketIdentifier":"XNAS",
                            "stockSymbol":"AMZN"
                        },{
                            "marketIdentifier":"XNAS",
                            "stockSymbol":"AMZN"
                        }]"""
        );
    }

    public String createWithListingsJsonPair(final String listingsJsonPair) {
        return String.format(
            """
                        {
                            "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                            "name":"Amazon",
                            "homeCountry":"US",
                            "functionalCurrency":"USD"
                            %s
                        }""",
            prependCommaIfNotEmpty(listingsJsonPair)
        );
    }

    public String createWithMarketIdJsonPair(final String marketIdJsonPair) {
        return String.format(
            """
                        {
                            "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                            "name":"Amazon",
                            "homeCountry":"US",
                            "functionalCurrency":"USD",
                            "listings":[{
                                %s"stockSymbol":"AMZN"
                            }]
                        }""",
            appendCommaIfNotEmpty(marketIdJsonPair)
        );
    }

    public String createWithSymbolJsonPair(final String jsonPair) {
        return String.format(
            """
                        {
                            "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                            "name":"Amazon",
                            "homeCountry":"US",
                            "functionalCurrency":"USD",
                            "listings":[{
                                "marketIdentifier":"XNAS"
                                %s
                            }]
                        }""",
            prependCommaIfNotEmpty(jsonPair)
        );
    }

    public String createWithFunctionalCurrencyJsonPair(final String jsonPair) {
        return String.format(
            """
                        {
                            "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                            "name":"Amazon",
                            "homeCountry":"US",
                            %s
                            "listings":[{
                                "marketIdentifier":"XNAS",
                                "stockSymbol":"AMZN"
                            }]
                        }""",
            appendCommaIfNotEmpty(jsonPair)
        );
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
    public void shouldCreateValidJson() throws JSONException {
        assertThatJson(factory.createAmazon())
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "name":"Amazon",
                    "homeCountry":"US",
                    "functionalCurrency":"USD",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithoutName() throws JSONException {
        assertThatJson(factory.createWithNameJsonPair(""))
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "homeCountry":"US",
                    "functionalCurrency":"USD",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithCustomNameJsonPair() throws JSONException {
        assertThatJson(factory.createWithNameJsonPair("\"name\":null"))
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "name":null,
                    "homeCountry":"US",
                    "functionalCurrency":"USD",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithoutHomeCountry() throws JSONException {
        assertThatJson(factory.createWithHomeCountryJsonPair(""))
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "name":"Amazon",
                    "functionalCurrency":"USD",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithCustomHomeCountryJsonPair() throws JSONException {
        assertThatJson(factory.createWithHomeCountryJsonPair("\"homeCountry\":null"))
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "name":"Amazon",
                    "homeCountry":null,
                    "functionalCurrency":"USD",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithoutListingsField() throws JSONException {
        assertThatJson(factory.createWithListingsJsonPair(""))
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "name":"Amazon",
                    "homeCountry":"US",
                    "functionalCurrency":"USD"
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithCustomListingsJsonPair() throws JSONException {
        assertThatJson(factory.createWithListingsJsonPair("\"listings\":null"))
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "name":"Amazon",
                    "homeCountry":"US",
                    "functionalCurrency":"USD",
                    "listings":null
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithMultipleListings() throws JSONException {
        assertThatJson(factory.createWithMultipleListings())
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "name":"Amazon",
                    "homeCountry":"US",
                    "functionalCurrency":"USD",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    },{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateListedCompanyWithoutMarketIdField() throws JSONException {
        assertThatJson(factory.createWithMarketIdJsonPair(""))
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "name":"Amazon",
                    "homeCountry":"US",
                    "functionalCurrency":"USD",
                    "listings":[{
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateListedCompanyWithCustomMarketIdField() throws JSONException {
        assertThatJson(factory.createWithMarketIdJsonPair("\"marketIdentifier\":\"London Stock Exchange\""))
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "name":"Amazon",
                    "homeCountry":"US",
                    "functionalCurrency":"USD",
                    "listings":[{
                        "marketIdentifier":"London Stock Exchange",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateListedCompanyWithoutSymbolField() throws JSONException {
        assertThatJson(factory.createWithSymbolJsonPair(""))
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "name":"Amazon",
                    "homeCountry":"US",
                    "functionalCurrency":"USD",
                    "listings":[{
                        "marketIdentifier":"XNAS"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateListedCompanyWithCustomSymbolField() throws JSONException {
        assertThatJson(factory.createWithSymbolJsonPair("\"stockSymbol\":\"Amazon\""))
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "name":"Amazon",
                    "homeCountry":"US",
                    "functionalCurrency":"USD",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"Amazon"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithoutFunctionalCurrency() throws JSONException {
        assertThatJson(factory.createWithFunctionalCurrencyJsonPair(""))
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "name":"Amazon",
                    "homeCountry":"US",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithCustomFunctionalCurrency() throws JSONException {
        assertThatJson(
            factory.createWithFunctionalCurrencyJsonPair("\"functionalCurrency\":\"United Stated Dollar\"")
        )
            .isEqualTo(
                """
                        {
                            "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                            "name":"Amazon",
                            "homeCountry":"US",
                            "functionalCurrency":"United Stated Dollar",
                            "listings":[{
                                "marketIdentifier":"XNAS",
                                "stockSymbol":"AMZN"
                            }]
                        }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithoutId() throws JSONException {
        assertThatJson(factory.createWithIdJsonPair(""))
            .isEqualTo(
                """
                {
                    "name":"Amazon",
                    "homeCountry":"US",
                    "functionalCurrency":"USD",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }

    @Test
    public void shouldCreateCompanyWithCustomCustomId() throws JSONException {
        assertThatJson(factory.createWithIdJsonPair("\"id\":\"custom UUID\""))
            .isEqualTo(
                """
                        {
                            "id":"custom UUID",
                            "name":"Amazon",
                            "homeCountry":"US",
                            "functionalCurrency":"USD",
                            "listings":[{
                                "marketIdentifier":"XNAS",
                                "stockSymbol":"AMZN"
                            }]
                        }"""
            );
    }

    @Test
    public void shouldCreateWithoutNameAndCurrency() throws JSONException {
        assertThatJson(factory.createWithoutNameAndCurrency())
            .isEqualTo(
                """
                {
                    "id":"339d7d9e-d837-47bd-971b-d52e965e6968",
                    "homeCountry":"US",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"AMZN"
                    }]
                }"""
            );
    }
}
