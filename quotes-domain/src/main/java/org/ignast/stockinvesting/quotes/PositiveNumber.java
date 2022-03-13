package org.ignast.stockinvesting.quotes;

import lombok.EqualsAndHashCode;

import javax.persistence.Embeddable;
import java.io.Serializable;

@EqualsAndHashCode
@Embeddable
public final class PositiveNumber implements Serializable {
    private int number;

    protected PositiveNumber() {
        //JPA requirement for entities
    }

    public PositiveNumber(int number) {
        this.number = number;
        if (number <= 0) {
            throw new IllegalArgumentException("Must be positive");
        }
    }

    public int get() {
        return number;
    }
}
