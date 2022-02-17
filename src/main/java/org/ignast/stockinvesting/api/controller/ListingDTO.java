package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ListingDTO {
    @NotNull
    private String stockExchange;

    @NotNull
    private String ticker;

    public ListingDTO(@JsonProperty(value = "stockExchange") String stockExchange,
            @JsonProperty(value = "ticker") String ticker) {
        this.stockExchange = stockExchange;
        this.ticker = ticker;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public String getTicker() {
        return ticker;
    }
}
