package org.ignast.stockinvesting.quotes.api.controller;

import lombok.val;
import org.ignast.stockinvesting.quotes.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class CompanyController {

    private Companies companies;
    private StockExchanges stockExchanges;

    public CompanyController(Companies companies, StockExchanges stockExchanges) {
        this.companies = companies;

        this.stockExchanges = stockExchanges;
    }

    @PutMapping(value = "/companies", consumes = VersionedApiMediaTypes.V1)
    public HttpEntity<String> createCompany(@Valid @RequestBody CompanyDTO companyDTO){
        Company company = companyDTO.getListings().stream().findFirst().map(l -> {
            val externalId = new PositiveNumber(companyDTO.getId());
            val name = new CompanyName(companyDTO.getName());
            val symbol = new StockSymbol(l.getStockSymbol());
            val stockExchange = stockExchanges.getFor(new MarketIdentifierCode(l.getMarketIdentifier()));
            return new Company(externalId, name, symbol, stockExchange);
        }).orElseThrow(() -> new IllegalArgumentException("Company to be created was expected to have one listing, but zero was found"));

        companies.create(company);
        return new ResponseEntity("", HttpStatus.CREATED);
    }
}
