package org.ignast.stockinvesting.quotes;

import lombok.*;
import org.javamoney.moneta.Money;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA requirement for entities
@Entity
public class Company {

    @Id
    @EqualsAndHashCode.Include
    @NonNull
    @Getter
    private int externalId;

    @Embedded
    @NonNull
    @Getter
    private CompanyName name;

    @Embedded
    @NonNull
    @Getter
    private StockSymbol stockSymbol;

    @Embedded
    @NonNull
    @Getter
    private StockExchange stockExchange;

    public Money getQuotedPrice() {
        return stockExchange.getQuotedPrice(stockSymbol);
    }
}
