package org.ignast.stockinvesting.api.controller;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

final class CompanyDTOTest {

    @Test
    public void shouldPreserveNonNestedAttributes() {
        val company = new CompanyDTO("someId", "Amazon", "United States", "United States Dollar", Collections.emptyList());
        assertThat(company.getId()).isEqualTo("someId");
        assertThat(company.getHomeCountry()).isEqualTo("United States");
        assertThat(company.getName()).isEqualTo("Amazon");
        assertThat(company.getFunctionalCurrency()).isEqualTo("United States Dollar");
    }

    @Test
    public void shouldPreserveListings() {
        List<ListingDTO> listings = Arrays.asList(new ListingDTO("New York Stock Exchange", "Amazon"));
        assertThat(anyCompanyWith(listings).getListings()).isEqualTo(listings);
    }

    @Test
    public void shouldPreserveMultipleListings() {
        List<ListingDTO> listings = Arrays.asList(new ListingDTO("New York Stock Exchange", "Amazon"),
                new ListingDTO("Hong Kong Stock Exchange", "Amazon"));
        assertThat(anyCompanyWith(listings).getListings()).isEqualTo(listings);
    }

    @Test
    public void shouldDropAnyIndividualNullListing() {
        ListingDTO listing = new ListingDTO("New York Stock Exchange", "Amazon");
        assertThat(anyCompanyWith(Arrays.asList(null, listing)).getListings()).isEqualTo(Arrays.asList(listing));
    }

    @Test
    public void shouldAllowNullFieldsToEnableJavaxValidation() {
        val company = new CompanyDTO(null, null, null, null, null);
        assertThat(company.getListings()).isNull();
        assertThat(company.getId()).isNull();
        assertThat(company.getName()).isNull();

        val listing = new ListingDTO(null, null);
        assertThat(listing.getMarketIdentifier()).isNull();
        assertThat(listing.getStockSymbol()).isNull();
    }


    private CompanyDTO anyCompanyWith(List<ListingDTO> listings) {
        return new CompanyDTO("anyId","anyName", "anyCountry", "United States Dollar", listings);
    }
}