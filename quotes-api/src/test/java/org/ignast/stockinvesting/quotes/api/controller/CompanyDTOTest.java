package org.ignast.stockinvesting.quotes.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.val;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public final class CompanyDTOTest {

    @Test
    public void shouldBeEqual() {
        EqualsVerifier.forClass(CompanyDTO.class).verify();
    }

    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldPreserveNonNestedAttributes() {
        final val company = new CompanyDTO(3, "Amazon", Collections.emptyList());
        assertThat(company.getId()).isEqualTo(3);
        assertThat(company.getName()).isEqualTo("Amazon");
    }

    @Test
    public void shouldPreserveListings() {
        final val listings = List.of(new ListingDTO("New York Stock Exchange", "Amazon"));
        assertThat(anyCompanyWith(listings).getListings()).isEqualTo(listings);
    }

    @Test
    public void shouldPreserveMultipleListings() {
        final val listings = Arrays.asList(
            new ListingDTO("New York Stock Exchange", "Amazon"),
            new ListingDTO("Hong Kong Stock Exchange", "Amazon")
        );
        assertThat(anyCompanyWith(listings).getListings()).isEqualTo(listings);
    }

    @Test
    public void shouldDropAnyIndividualNullListing() {
        final val listing = new ListingDTO("New York Stock Exchange", "Amazon");
        assertThat(anyCompanyWith(Arrays.asList(null, listing)).getListings()).isEqualTo(List.of(listing));
    }

    @Test
    public void shouldAllowNullFieldsToEnableJavaxValidation() {
        final val company = new CompanyDTO(null, null, null);
        assertThat(company.getListings()).isNull();
        assertThat(company.getId()).isNull();
        assertThat(company.getName()).isNull();

        final val listing = new ListingDTO(null, null);
        assertThat(listing.getMarketIdentifier()).isNull();
        assertThat(listing.getStockSymbol()).isNull();
    }

    @SuppressWarnings("checkstyle:magicnumber")
    private CompanyDTO anyCompanyWith(final List<ListingDTO> listings) {
        return new CompanyDTO(4, "anyName", listings);
    }
}
