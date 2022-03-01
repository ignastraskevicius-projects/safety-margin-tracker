package com.ignast.stockinvesting.estimates.alphavantagesim;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class AlphaVantageStub {

    private WireMockExtension wireMock;

    public AlphaVantageStub(WireMockExtension wireMock) {

        this.wireMock = wireMock;
    }

    public void stubPriceForAllSymbolsButAAAA() {
        wireMock.stubFor(any(urlPathEqualTo("/query"))
                .willReturn(ok().withBody("{\"Error Message\":\"Some human-readable error message\"}")
                        .withHeader("Content-Type", "application/json")));
        wireMock.stubFor(any(urlPathEqualTo("/query")).withQueryParam("function", equalTo("GLOBAL_QUOTE"))
                .withQueryParam("symbol", matching(".+")).withQueryParam("apikey", matching(".+"))
                .willReturn(ok().withBody("{\"Global Quote\":{\"05. price\":\"128.5000\"}}").withHeader("Content-Type",
                        "application/json")));
        wireMock.stubFor(any(urlPathEqualTo("/query")).withQueryParam("function", equalTo("GLOBAL_QUOTE"))
                .withQueryParam("symbol", matching("AAAA")).withQueryParam("apikey", matching(".+"))
                .willReturn(ok().withBody("{\"Global Quote\":{}}").withHeader("Content-Type", "application/json")));
    }
}