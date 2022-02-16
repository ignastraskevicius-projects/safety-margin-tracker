package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ListingDTOTest {

    @ParameterizedTest
    @ValueSource(strings = { "New York Stock Exchange", "London Stock Exchange" })
    public void shouldPreserveStockExchange(String stockExchange) {
        assertThat(new ListingDTO(stockExchange).getStockExchange()).isEqualTo(stockExchange);
    }
}