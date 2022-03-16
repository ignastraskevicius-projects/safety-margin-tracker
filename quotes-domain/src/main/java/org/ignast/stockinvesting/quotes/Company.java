package org.ignast.stockinvesting.quotes;

import lombok.*;
import org.javamoney.moneta.Money;

import javax.persistence.*;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA requirement for entities
@Entity
public class Company {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    @Getter
    @Embedded
    @AttributeOverride(name = "number", column = @Column(name = "externalId"))
    private CompanyExternalId externalId;

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
