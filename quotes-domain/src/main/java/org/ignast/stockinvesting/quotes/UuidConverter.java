package org.ignast.stockinvesting.quotes;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.UUID;

@Converter(autoApply = true)
public class UuidConverter implements AttributeConverter<UUID, String> {

    @Override
    public String convertToDatabaseColumn(UUID uuid) {
        return uuid.toString();
    }

    @Override
    public UUID convertToEntityAttribute(String uuid) {
        return UUID.fromString(uuid);
    }
}