package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressDTO {

    public AddressDTO(@JsonProperty(value = "country", required = true) String country) {

    }
}
