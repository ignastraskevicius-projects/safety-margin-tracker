package org.ignast.stockinvesting.quotes.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class CompanyDTO {

    @NotNull
    @DomainClassConstraint(domainClass = UUID.class)
    private String id;

    @NotNull
    @Size(min = 1, max = 255, message = "Company name must be between {min}-{max} characters")
    private String name;

    @NotNull
    @Size(min = 1, message = "Company must be listed on at least 1 stock exchange")
    @Size(max = 1, message = "Multiple listings are not supported")
    @Valid
    private List<ListingDTO> listings;

    public CompanyDTO(
            @JsonProperty(value = "id") String id,
            @JsonProperty(value = "name") String name,
            @JsonProperty(value = "listings") List<ListingDTO> listings) {
        this.id = id;
        this.name = name;
        if (listings != null) {
            this.listings = listings.stream().filter(Objects::nonNull).collect(Collectors.toList());
        }
    }
}
