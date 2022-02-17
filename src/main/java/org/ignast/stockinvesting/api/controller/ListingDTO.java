package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ListingDTO {
    @NotNull
    private String stockExchange;

    public ListingDTO(@JsonProperty(value = "stockExchange") String stockExchange) {
        this.stockExchange = stockExchange;
    }

    public String getStockExchange() {
        return stockExchange;
    }
}
