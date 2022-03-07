package org.ignast.stockinvesting.api.controller;

import lombok.val;
import org.ignast.stockinvesting.domain.*;
import org.ignast.stockinvesting.estimates.domain.Company;
import org.ignast.stockinvesting.estimates.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.estimates.domain.StockQuotes;
import org.ignast.stockinvesting.estimates.domain.StockSymbol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Currency;

@RestController
@RequestMapping("companies")
public class CompanyController {

    private Companies companies;

    @Autowired
    private StockQuotes quotes;

    public CompanyController(Companies companies) {
        this.companies = companies;
    }

    @PostMapping(consumes = VersionedApiMediaTypes.V1)
    public HttpEntity<String> defineCompany(@Validated @RequestBody CompanyDTO company) {
        val currency = Currency.getInstance(company.getFunctionalCurrency());
        companies.create(new Company(company.getName(), company.getHomeCountry(), currency));
        company.getListings().stream().forEach(l -> quotes.getQuotedPriceOf(new StockSymbol(l.getStockSymbol()),
                new MarketIdentifierCode(l.getMarketIdentifier())));

        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

}
