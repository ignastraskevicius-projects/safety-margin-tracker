package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.ignast.stockinvesting.estimates.domain.CountryCode;
import org.ignast.stockinvesting.estimates.domain.CurrencyCode;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class CompanyDTO {

    @NotNull
    @Size(min = 1, max = 255, message = "Company name must be between {min}-{max} characters")
    private String name;

    @NotNull
    @DomainClassConstraint(domainClass = CountryCode.class)
    private String homeCountry;

    @NotNull
    @DomainClassConstraint(domainClass = CurrencyCode.class)
    private String functionalCurrency;

    @NotNull
    @Size(min = 1, message = "Company must be listed on at least 1 stock exchange")
    @Size(max = 1, message = "Multiple listings are not supported")
    @Valid
    private List<ListingDTO> listings;

    public CompanyDTO(@JsonProperty(value = "name") String name,
            @JsonProperty(value = "homeCountry") String homeCountry,
            @JsonProperty("functionalCurrency") String functionalCurrency,
            @JsonProperty(value = "listings") List<ListingDTO> listings) {
        this.name = name;
        this.homeCountry = homeCountry;
        this.functionalCurrency = functionalCurrency;
        if (listings != null) {
            this.listings = listings.stream().filter(Objects::nonNull).collect(Collectors.toList());
        }
    }
}
