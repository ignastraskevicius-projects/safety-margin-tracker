package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ListingDTOTest {

    @ParameterizedTest
    @ValueSource(strings = { "New York Stock Exchange", "London Stock Exchange" })
    public void shouldPreserveMarketId(String marketId) {
        assertThat(new ListingDTO(marketId, "anySymbol").getMarketIdentifier()).isEqualTo(marketId);
    }

    @ParameterizedTest
    @ValueSource(strings = { "Amazon", "Alibaba" })
    public void shouldPreserveStockSymbol(String symbol) {
        assertThat(new ListingDTO("New York Stock Exchange", symbol).getStockSymbol()).isEqualTo(symbol);
    }
}