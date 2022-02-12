package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyDTO {

    private final AddressDTO address;

    public CompanyDTO(@JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "address", required = true) AddressDTO address) {
        this.address = address;
    }
}
