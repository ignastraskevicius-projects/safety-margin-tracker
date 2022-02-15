package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class AddressDTO {

    @NotNull
    private String country;

    public AddressDTO(@JsonProperty(value = "country", required = true) String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }
}
