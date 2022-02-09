package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class CompanyDTO {

    @NotNull
    private final AddressDTO address;

    public CompanyDTO(@JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "address", required = true) AddressDTO address) {
        this.address = address;
    }
}
