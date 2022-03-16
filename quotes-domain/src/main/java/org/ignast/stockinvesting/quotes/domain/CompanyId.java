package org.ignast.stockinvesting.quotes.domain;

import lombok.NonNull;

import java.util.UUID;

public class CompanyId {

    public static UUID toUUID(@NonNull String id) {
        if (id.length() != 36) {
            throw new IllegalArgumentException("Must consist of 36 characters");
        }
        if (!id.matches("^[A-Fa-f0-9-]*$")) {
            throw new IllegalArgumentException("Must consist of hyphens (-) and a,b,c,d,e,f and numeric characters only");
        }
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Must be a valid UUID");
        }
    }
}
