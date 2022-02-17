package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class CompanyDTO {

    @NotNull
    @Size(min = 1, max = 255, message = "Company name must be between {min}-{max} characters")
    private String name;

    @NotNull
    @Valid
    private AddressDTO address;

    @NotNull
    @Size(min = 1, message = "Company must be listed on at least 1 stock exchange")
    private List<Integer> listings;

    public CompanyDTO(@JsonProperty(value = "name") String name, @JsonProperty(value = "address") AddressDTO address,
            @JsonProperty(value = "listings") List<Integer> listings) {
        this.name = name;
        this.address = address;
        this.listings = listings;
    }

    public AddressDTO getAddress() {
        return address;
    }
}
