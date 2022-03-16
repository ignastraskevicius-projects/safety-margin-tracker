package org.ignast.stockinvesting.quotes.api.controller;

import lombok.val;
import org.ignast.stockinvesting.quotes.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static java.util.Arrays.asList;
import static org.ignast.stockinvesting.quotes.api.controller.VersionedApiMediaTypes.V1;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private Companies companies;
    private StockExchanges stockExchanges;

    public CompanyController(Companies companies, StockExchanges stockExchanges) {
        this.companies = companies;
        this.stockExchanges = stockExchanges;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @PutMapping(consumes = V1, produces = V1)
    public EntityModel<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO companyDTO){
        val externalId = companyDTO.getId();
        companies.create(mapFromDto(companyDTO));
        val selfLink = linkTo(CompanyController.class).slash(externalId).withSelfRel();
        return EntityModel.of(companyDTO, selfLink);
    }

    @GetMapping(value = "/{id}", produces = V1)
    public EntityModel<CompanyDTO> retrieveCompanyById(@PathVariable int id) {
        val company = companies.findByExternalId(new CompanyExternalId(id));
        val selfLink = linkTo(CompanyController.class).slash(id).withSelfRel();

        return EntityModel.of(mapToDto(company), selfLink);
    }

    private Company mapFromDto(CompanyDTO companyDTO) {
        Company company = companyDTO.getListings().stream().findFirst().map(l -> {
            val externalId = new CompanyExternalId(companyDTO.getId());
            val name = new CompanyName(companyDTO.getName());
            val symbol = new StockSymbol(l.getStockSymbol());
            val stockExchange = stockExchanges.getFor(new MarketIdentifierCode(l.getMarketIdentifier()));
            return new Company(externalId, name, symbol, stockExchange);
        }).orElseThrow(() -> new IllegalArgumentException("Company to be created was expected to have one listing, but zero was found"));
        return company;
    }


    private CompanyDTO mapToDto(Company company) {
        val listingDto = new ListingDTO(company.getStockExchange().getMarketIdentifierCode().get(), company.getStockSymbol().get());
        val companyDto = new CompanyDTO(company.getExternalId().get(), company.getName().get(), asList(listingDto));
        return companyDto;
    }
}
