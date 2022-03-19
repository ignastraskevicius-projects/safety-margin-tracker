package org.ignast.stockinvesting.quotes.domain;

import static java.lang.String.format;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.val;

@EqualsAndHashCode
@Embeddable
public class CompanyName {

    private static final int MAX_LENGTH_FOUND_IN_UK = 160;

    @Column(name = "company_name")
    private String name;

    protected CompanyName() {
        //constructor for JPA
    }

    public CompanyName(@NonNull final String name) {
        if (name.isEmpty() || name.length() > MAX_LENGTH_FOUND_IN_UK) {
            final val message = format(
                "Company name must be between 1-%s characters",
                MAX_LENGTH_FOUND_IN_UK
            );
            throw new IllegalArgumentException(message);
        }
        this.name = name;
    }

    public String get() {
        return name;
    }
}
