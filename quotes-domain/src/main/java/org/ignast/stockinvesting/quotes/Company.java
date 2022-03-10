package org.ignast.stockinvesting.quotes;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.javamoney.moneta.Money;

import javax.persistence.Entity;
import java.util.UUID;

@EqualsAndHashCode
@Entity
public class Company {
    private UUID id;
    private CompanyName name;
    private StockSymbol symbol;
    private StockExchange stockExchange;

    public Company(@NonNull UUID id, @NonNull CompanyName name, @NonNull StockSymbol symbol, @NonNull StockExchange stockExchange) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.stockExchange = stockExchange;
    }

    public Money getQuotedPrice() {
        return stockExchange.getQuotedPrice(symbol);
    }

    public UUID getId() {
        return id;
    }

    public CompanyName getName() {
        return name;
    }

    public StockSymbol getSockSymbol() {
        return symbol;
    }
}
