package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.ignast.stockinvesting.estimates.domain.CompanyName;
import org.ignast.stockinvesting.estimates.domain.CountryCode;
import org.ignast.stockinvesting.estimates.domain.CurrencyCode;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;

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

    public CompanyDTO(
        @JsonProperty(value = "id") final String id,
        @JsonProperty(value = "name") final String name,
        @JsonProperty(value = "homeCountry") final String homeCountry,
        @JsonProperty("functionalCurrency") final String functionalCurrency
    ) {
        this.id = id;
        this.name = name;
        this.homeCountry = homeCountry;
        this.functionalCurrency = functionalCurrency;
    }
}
