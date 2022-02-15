package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class CompanyDTO {

    @NotEmpty
    private String name;

    public CompanyDTO(@JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "address", required = true) AddressDTO address) {
        this.name = name;
    }
}
