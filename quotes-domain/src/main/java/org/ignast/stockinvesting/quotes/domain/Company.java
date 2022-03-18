package org.ignast.stockinvesting.quotes.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.javamoney.moneta.Money;

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

    public static Company create(
        final CompanyExternalId externalId,
        final CompanyName name,
        final StockSymbol stockSymbol,
        final StockExchange stockExchange
    ) {
        final val company = new Company(externalId, name, stockSymbol, stockExchange);
        verifyStockSymbolIsSupported(company);
        return company;
    }

    private static void verifyStockSymbolIsSupported(final Company company) {
        company.getQuotedPrice();
    }

    public Money getQuotedPrice() {
        return stockExchange.getQuotedPrice(stockSymbol);
    }
}
