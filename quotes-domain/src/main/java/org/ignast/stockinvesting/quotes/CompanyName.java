package org.ignast.stockinvesting.quotes;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@EqualsAndHashCode
@Embeddable
public final class CompanyName {

    @Column(name = "company_name")
    private String name;

    protected CompanyName() {
        //constructor for JPA
    }

    public CompanyName(@NonNull String name) {
        if (name.isEmpty() || name.length() > 255) {
            throw new IllegalArgumentException("Company name must be between 1-255 characters");
        }
        this.name = name;
    }

    public String get() {
        return name;
    }
}
