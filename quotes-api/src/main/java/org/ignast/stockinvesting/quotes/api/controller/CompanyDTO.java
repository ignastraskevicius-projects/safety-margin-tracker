package org.ignast.stockinvesting.quotes.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.ignast.stockinvesting.quotes.CompanyName;
import org.ignast.stockinvesting.quotes.CompanyExternalId;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@EqualsAndHashCode
@Getter
public final class CompanyDTO {

    @NotNull
    @DomainClassConstraint(domainClass = CompanyExternalId.class)
    private final Integer id;

    @NotNull
    @DomainClassConstraint(domainClass = CompanyName.class)
    private final String name;

    @NotNull
    @Size(min = 1, message = "Company must be listed on at least 1 stock exchange")
    @Size(max = 1, message = "Multiple listings are not supported")
    @Valid
    private final List<ListingDTO> listings;

    public CompanyDTO(
            @JsonProperty(value = "id") Integer id,
            @JsonProperty(value = "name") String name,
            @JsonProperty(value = "listings") List<ListingDTO> listings) {
        this.id = id;
        this.name = name;
        this.listings = listings != null ? listings.stream().filter(Objects::nonNull).collect(Collectors.toList()) : null;
    }
}
