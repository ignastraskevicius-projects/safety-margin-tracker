package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CompanyDTO {

    @Size(min = 1)
    private String name;

    public CompanyDTO(@JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "address", required = true) AddressDTO address) {
        this.name = name;
    }
}
