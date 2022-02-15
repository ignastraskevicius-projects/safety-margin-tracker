package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class AddressDTOTest {

    @ParameterizedTest
    @ValueSource(strings = { "United States", "Germany" })
    public void shouldPreserveHomeCountry(String country) {
        assertThat(new AddressDTO(country).getCountry()).isEqualTo(country);
    }
}