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
    public void shouldPreserveHomeCountry(String country) {
        assertThat(new CompanyDTO("anyName", country, "United States Dollar",
                Arrays.asList(new ListingDTO("New York Stock Exchange", "Amazon"))).getHomeCountry())
                        .isEqualTo(country);
    }

    @Test
    public void shouldPreserveListings() {
        List<ListingDTO> listings = Arrays.asList(new ListingDTO("New York Stock Exchange", "Amazon"));
        assertThat(new CompanyDTO("anyName", "anyCountry", "United States Dollar", listings).getListings())
                .isEqualTo(listings);
    }

    @Test
    public void shouldPreserveMultiplelistings() {
        List<ListingDTO> listings = Arrays.asList(new ListingDTO("New York Stock Exchange", "Amazon"),
                new ListingDTO("Hong Kong Stock Exchange", "Amazon"));
        assertThat(new CompanyDTO("anyName", "anyCountry", "United States Dollar", listings).getListings())
                .isEqualTo(listings);
    }

    @Test
    public void shouldDropAnyIndividualNullListing() {
        ListingDTO listing = new ListingDTO("New York Stock Exchange", "Amazon");
        assertThat(new CompanyDTO("anyName", "anyCountry", "United States Dollar", Arrays.asList(null, listing))
                .getListings()).isEqualTo(Arrays.asList(listing));
    }

    @Test
    public void shouldAllowNullListingsToEnableJavaxValidation() {
        assertThat(new CompanyDTO("anyName", "anyCountry", "United States Dollar", null).getListings()).isEqualTo(null);
    }
}