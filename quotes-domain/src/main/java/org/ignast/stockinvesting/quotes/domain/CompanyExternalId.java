package org.ignast.stockinvesting.quotes.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

import javax.persistence.Embeddable;

@EqualsAndHashCode
@Embeddable
@ToString
public final class CompanyExternalId implements Serializable {
    private int number;

    protected CompanyExternalId() {
        //JPA requirement for entities
    }

    public CompanyExternalId(int number) {
        this.number = number;
        if (number <= 0) {
            throw new IllegalArgumentException("Must be positive");
        }
    }

    public int get() {
        return number;
    }
}
