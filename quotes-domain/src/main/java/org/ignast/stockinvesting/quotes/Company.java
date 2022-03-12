package org.ignast.stockinvesting.quotes;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.javamoney.moneta.Money;

import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Company {
    @Id
    @Convert(converter = UuidConverter.class)
    @EqualsAndHashCode.Include
    private UUID id;
    @Embedded
    private CompanyName name;
    @Embedded
    private StockSymbol symbol;
    @Embedded
    private StockExchange stockExchange;

    protected Company() {
        //constructor for JPA
    }

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
