package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyDTOTest {

    @ParameterizedTest
    @ValueSource(strings = { "United States", "Germany" })
    public void shouldPreserveAddress(String country) {
        assertThat(new CompanyDTO("anyName", new AddressDTO(country), Arrays.asList(4)).getAddress().getCountry())
                .isEqualTo(country);
    }

}