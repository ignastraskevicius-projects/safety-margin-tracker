package org.ignast.stockinvesting.quotes;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@EqualsAndHashCode
@Embeddable
public final class MarketIdentifierCode {
    @Column(name = "market_identifier_code")
    private String code;

    protected MarketIdentifierCode() {
        //JPA requirement to have default constructor
    }

    public MarketIdentifierCode(@NonNull String code) {
        if (code.length() != 4) {
            throw new IllegalArgumentException("Market Identifier is not 4 characters long (ISO 10383 standard)");
        }
        if (!code.matches("^[A-Z]*$")) {
            throw new IllegalArgumentException(
                    "Market Identifier must contain only latin uppercase alphanumeric characters (ISO 10383 standard)");
        }
        this.code = code;
    }

    public String get() {
        return code;
    }
}
