package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ListingDTO {
    @NotNull
    private Integer stockExchange;

    public ListingDTO(@JsonProperty(value = "stockExchange") Integer stockExchange) {
        this.stockExchange = stockExchange;
    }

    public Integer getStockExchange() {
        return stockExchange;
    }
}
