package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.ignast.stockinvesting.estimates.domain.CompanyName;
import org.ignast.stockinvesting.estimates.domain.CountryCode;
import org.ignast.stockinvesting.estimates.domain.CurrencyCode;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public final class CompanyDTO {

    @NotNull
    @DomainClassConstraint(domainClass = UUID.class)
    private final String id;

    @NotNull
    @DomainClassConstraint(domainClass = CompanyName.class)
    private final String name;

    @NotNull
    @DomainClassConstraint(domainClass = CountryCode.class)
    private final String homeCountry;

    @NotNull
    @DomainClassConstraint(domainClass = CurrencyCode.class)
    private final String functionalCurrency;

    @NotNull
    @Size(min = 1, message = "Company must be listed on at least 1 stock exchange")
    @Size(max = 1, message = "Multiple listings are not supported")
    @Valid
    private List<ListingDTO> listings;

    public CompanyDTO(
            @JsonProperty(value = "id") final String id,
            @JsonProperty(value = "name") final String name,
            @JsonProperty(value = "homeCountry") final String homeCountry,
            @JsonProperty("functionalCurrency") final String functionalCurrency,
            @JsonProperty(value = "listings") final List<ListingDTO> listings) {
        this.id = id;
        this.name = name;
        this.homeCountry = homeCountry;
        this.functionalCurrency = functionalCurrency;
        if (listings != null) {
            this.listings = listings.stream().filter(Objects::nonNull).collect(Collectors.toList());
        }
    }
}
