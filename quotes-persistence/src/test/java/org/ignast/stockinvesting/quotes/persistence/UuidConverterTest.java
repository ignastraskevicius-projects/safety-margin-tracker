package org.ignast.stockinvesting.quotes.persistence;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UuidConverterTest {

    private final UUID uuid = UUID.fromString("388ee2e4-c528-42b7-885b-dba0a52f581a");

    private final String uuidBackingString = "388ee2e4-c528-42b7-885b-dba0a52f581a";

    private UuidConverter converter = new UuidConverter();

    @Test
    public void shouldRepresentAsBackingString() {
        assertThat(converter.convertToDatabaseColumn(uuid)).isEqualTo(uuidBackingString);
    }

    @Test
    public void shouldReconstructFromRepresentation() {
        assertThat(converter.convertToEntityAttribute(uuidBackingString)).isEqualTo(uuid);
    }
}