package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ListingDTOTest {

    @ParameterizedTest
    @ValueSource(strings = { "New York Stock Exchange", "London Stock Exchange" })
    public void shouldPreserveStockExchange(String stockExchange) {
        assertThat(new ListingDTO(stockExchange, "anyTicker").getStockExchange()).isEqualTo(stockExchange);
    }

    @ParameterizedTest
    @ValueSource(strings = { "Amazon", "Alibaba" })
    public void shouldPreserveTicker(String ticker) {
        assertThat(new ListingDTO("New York Stock Exchange", ticker).getTicker()).isEqualTo(ticker);
    }
}