package org.ignast.stockinvesting.quotes.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
@Embeddable
public class CompanyName {

    @Column(name = "company_name")
    private String name;

    protected CompanyName() {
        //constructor for JPA
    }

    public CompanyName(@NonNull final String name) {
        if (name.isEmpty() || name.length() > 255) {
            throw new IllegalArgumentException("Company name must be between 1-255 characters");
        }
        this.name = name;
    }

    public String get() {
        return name;
    }
}
