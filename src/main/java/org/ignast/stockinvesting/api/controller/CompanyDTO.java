package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CompanyDTO {

    @NotNull
    @Size(min = 1, max = 255, message = "Company name must be between {min}-{max} characters")
    private String name;

    @NotNull
    @Valid
    private AddressDTO address;

    @NotNull
    @Size(min = 3, message = "Currency must have 3 letters (ISO 4217)")
    private String functionalCurrency;

    @NotNull
    @Size(min = 1, message = "Company must be listed on at least 1 stock exchange")
    @Size(max = 1, message = "Multiple listings are not supported")
    @Valid
    private List<ListingDTO> listings;

    public CompanyDTO(@JsonProperty(value = "name") String name, @JsonProperty(value = "address") AddressDTO address,
            @JsonProperty("functionalCurrency") String functionalCurrency,
            @JsonProperty(value = "listings") List<ListingDTO> listings) {
        this.name = name;
        this.address = address;
        this.functionalCurrency = functionalCurrency;
        if (listings != null) {
            this.listings = listings.stream().filter(Objects::nonNull).collect(Collectors.toList());
        }
    }

    public AddressDTO getAddress() {
        return address;
    }

    public List<ListingDTO> getListings() {
        return listings;
    }
}
