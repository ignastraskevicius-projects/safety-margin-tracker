package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ListingDTO {
    @NotNull
    private String stockExchange;

    @NotNull
    private String stockSymbol;

    public ListingDTO(@JsonProperty(value = "stockExchange") String stockExchange,
            @JsonProperty(value = "stockSymbol") String stockSymbol) {
        this.stockExchange = stockExchange;
        this.stockSymbol = stockSymbol;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }
}
