package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyDTO {
    public CompanyDTO(@JsonProperty(value = "name", required = true) String name) {
    }
}
