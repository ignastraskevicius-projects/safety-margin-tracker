package org.ignast.stockinvesting.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

final class ListingDTOTest {

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
