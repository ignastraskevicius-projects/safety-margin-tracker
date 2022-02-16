package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ListingDTOTest {

    @ParameterizedTest
    @ValueSource(ints = { 3, 4 })
    public void shouldPreserveStockExchange(int stockExchange) {
        assertThat(new ListingDTO(stockExchange).getStockExchange()).isEqualTo(stockExchange);
    }
}