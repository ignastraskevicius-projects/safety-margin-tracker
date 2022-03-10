package org.ignast.stockinvesting.quotes;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
public final class CompanyName {

    private final String name;

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
