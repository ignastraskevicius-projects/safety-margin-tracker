package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyDTOTest {

    @ParameterizedTest
    @ValueSource(strings = { "United States", "Germany" })
    public void shouldPreserveAddress(String country) {
        assertThat(new CompanyDTO("anyName", new AddressDTO(country), Arrays.asList(4)).getAddress().getCountry())
                .isEqualTo(country);
    }

    @ParameterizedTest
    @ValueSource(ints = { 3, 4 })
    public void shouldPreserveListings(int listing) {
        List<Integer> listings = Arrays.asList(listing);
        assertThat(new CompanyDTO("anyName", new AddressDTO("anyCountry"), listings).getListings()).isEqualTo(listings);
    }

    @Test
    public void shouldPreserveMultiplelistings() {
        List<Integer> listings = Arrays.asList(3, 4);
        assertThat(new CompanyDTO("anyName", new AddressDTO("anyCountry"), listings).getListings()).isEqualTo(listings);
    }

    @Test
    public void shouldDropAnyIndividualNullListing() {
        assertThat(new CompanyDTO("anyName", new AddressDTO("anyCountry"), Arrays.asList(null, 3)).getListings())
                .isEqualTo(Arrays.asList(3));
    }

    @Test
    public void shouldListingsToEnableJavaxValidation() {
        assertThat(new CompanyDTO("anyName", new AddressDTO("anyCountry"), null).getListings()).isEqualTo(null);
    }
}