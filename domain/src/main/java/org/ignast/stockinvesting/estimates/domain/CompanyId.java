package org.ignast.stockinvesting.estimates.domain;

import java.util.UUID;
import lombok.NonNull;
import lombok.val;

public final class CompanyId {

    private static final int UUID_LENGTH = 36;

    private CompanyId() {}

    public static UUID toUUID(@NonNull final String id) {
        expecte36Characters(id);
        expectHexCharactersOnly(id);
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Must be a valid UUID");
        }
    }

    private static void expectHexCharactersOnly(final String id) {
        final val allowedCharacters =
            "Must consist of hyphens (-) and a,b,c,d,e,f and numeric characters only";
        if (!id.matches("^[A-Fa-f0-9-]*$")) {
            throw new IllegalArgumentException(allowedCharacters);
        }
    }

    private static void expecte36Characters(final String id) {
        if (id.length() != UUID_LENGTH) {
            throw new IllegalArgumentException("Must consist of 36 characters");
        }
    }
}
