package org.ignast.stockinvesting.api.controller;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;

final class CompanyDTOTest {

    @Test
    public void shouldPreserveNonNestedAttributes() {
        final val company = new CompanyDTO("someId", "Amazon", "US", "USD", Collections.emptyList());
        assertThat(company.getId()).isEqualTo("someId");
        assertThat(company.getHomeCountry()).isEqualTo("US");
        assertThat(company.getName()).isEqualTo("Amazon");
        assertThat(company.getFunctionalCurrency()).isEqualTo("USD");
    }

    @Test
    public void shouldPreserveListings() {
        final val listings = List.of(new ListingDTO("New York Stock Exchange", "Amazon"));
        assertThat(anyCompanyWith(listings).getListings()).isEqualTo(listings);
    }

    @Test
    public void shouldPreserveMultipleListings() {
        final val listings = asList(
            new ListingDTO("New York Stock Exchange", "Amazon"),
            new ListingDTO("Hong Kong Stock Exchange", "Amazon")
        );
        assertThat(anyCompanyWith(listings).getListings()).isEqualTo(listings);
    }

    @Test
    public void shouldDropAnyIndividualNullListing() {
        final val listing = new ListingDTO("New York Stock Exchange", "Amazon");
        assertThat(anyCompanyWith(asList(null, listing)).getListings()).isEqualTo(List.of(listing));
    }

    @Test
    public void shouldAllowNullFieldsToEnableJavaxValidation() {
        final val company = new CompanyDTO(null, null, null, null, null);
        assertThat(company.getListings()).isNull();
        assertThat(company.getId()).isNull();
        assertThat(company.getName()).isNull();

        final val listing = new ListingDTO(null, null);
        assertThat(listing.getMarketIdentifier()).isNull();
        assertThat(listing.getStockSymbol()).isNull();
    }

    private CompanyDTO anyCompanyWith(final List<ListingDTO> listings) {
        return new CompanyDTO("anyId", "anyName", "anyCountry", "United States Dollar", listings);
    }
}
