package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.ignast.stockinvesting.estimates.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.estimates.domain.StockSymbol;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;

import javax.validation.constraints.NotNull;

@Getter
public final class ListingDTO {
    @NotNull
    @DomainClassConstraint(domainClass = MarketIdentifierCode.class)
    private final String marketIdentifier;

    @NotNull
    @DomainClassConstraint(domainClass = StockSymbol.class)
    private final String stockSymbol;

    public ListingDTO(@JsonProperty(value = "marketIdentifier") String marketIdentifier,
            @JsonProperty(value = "stockSymbol") String stockSymbol) {
        this.marketIdentifier = marketIdentifier;
        this.stockSymbol = stockSymbol;
    }
}
