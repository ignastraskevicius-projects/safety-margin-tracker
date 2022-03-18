package org.ignast.stockinvesting.quotes.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public final class ListingDTOTest {

    @Test
    public void shouldBeEqual() {
        EqualsVerifier.forClass(ListingDTO.class).verify();
    }

    @ParameterizedTest
    @ValueSource(strings = { "New York Stock Exchange", "London Stock Exchange" })
    public void shouldPreserveMarketId(final String marketId) {
        assertThat(new ListingDTO(marketId, "anySymbol").getMarketIdentifier()).isEqualTo(marketId);
    }

    @ParameterizedTest
    @ValueSource(strings = { "Amazon", "Alibaba" })
    public void shouldPreserveStockSymbol(final String symbol) {
        assertThat(new ListingDTO("New York Stock Exchange", symbol).getStockSymbol()).isEqualTo(symbol);
    }
}
