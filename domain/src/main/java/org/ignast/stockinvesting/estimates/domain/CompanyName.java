package org.ignast.stockinvesting.estimates.domain;

import static java.lang.String.format;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.val;

@EqualsAndHashCode
public final class CompanyName {

    private static final int MAX_LENGTH_FOUND_IN_UK = 160;

    private final String name;

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
