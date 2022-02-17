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
        assertThat(new CompanyDTO("anyName", new AddressDTO(country), Arrays.asList(new ListingDTO(3))).getAddress()
                .getCountry()).isEqualTo(country);
    }

    @Test
    public void shouldPreserveListings() {
        List<ListingDTO> listings = Arrays.asList(new ListingDTO(3));
        assertThat(new CompanyDTO("anyName", new AddressDTO("anyCountry"), listings).getListings()).isEqualTo(listings);
    }

    @Test
    public void shouldPreserveMultiplelistings() {
        List<ListingDTO> listings = Arrays.asList(new ListingDTO(3), new ListingDTO(4));
        assertThat(new CompanyDTO("anyName", new AddressDTO("anyCountry"), listings).getListings()).isEqualTo(listings);
    }

    @Test
    public void shouldDropAnyIndividualNullListing() {
        ListingDTO listing = new ListingDTO(3);
        assertThat(new CompanyDTO("anyName", new AddressDTO("anyCountry"), Arrays.asList(null, listing)).getListings())
                .isEqualTo(Arrays.asList(listing));
    }

    @Test
    public void shouldListingsToEnableJavaxValidation() {
        assertThat(new CompanyDTO("anyName", new AddressDTO("anyCountry"), null).getListings()).isEqualTo(null);
    }
}