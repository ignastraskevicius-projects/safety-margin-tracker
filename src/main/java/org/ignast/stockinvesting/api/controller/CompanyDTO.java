package org.ignast.stockinvesting.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

public class CompanyDTO {

    @NotNull
    private final List<CompanyDTO> address;

    public CompanyDTO(@JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "address", required = true) List<CompanyDTO> address) {
        this.address = address;
    }
}
