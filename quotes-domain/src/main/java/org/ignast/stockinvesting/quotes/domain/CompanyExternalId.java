package org.ignast.stockinvesting.quotes.domain;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@Embeddable
@ToString
public class CompanyExternalId implements Serializable {

    private int number;

    protected CompanyExternalId() {
        //JPA requirement for entities
    }

    public CompanyExternalId(final int number) {
        this.number = number;
        if (number <= 0) {
            throw new IllegalArgumentException("Must be positive");
        }
    }

    public int get() {
        return number;
    }
}
