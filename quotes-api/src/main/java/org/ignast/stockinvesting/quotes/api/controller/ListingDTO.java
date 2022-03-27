package org.ignast.stockinvesting.quotes.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.annotation.DomainClassConstraint;

@EqualsAndHashCode
public final class ListingDTO {

    @NotNull
    @DomainClassConstraint(domainClass = MarketIdentifierCode.class)
    private final String marketIdentifier;

    @NotNull
    @DomainClassConstraint(domainClass = StockSymbol.class)
    private final String stockSymbol;

    public ListingDTO(
        @JsonProperty(value = "marketIdentifier") final String marketIdentifier,
        @JsonProperty(value = "stockSymbol") final String stockSymbol
    ) {
        this.marketIdentifier = marketIdentifier;
        this.stockSymbol = stockSymbol;
    }

    public String getMarketIdentifier() {
        return marketIdentifier;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }
}
