package org.ignast.stockinvesting.quotes.api.controller;

import static org.ignast.stockinvesting.quotes.api.controller.VersionedApiMediaTypes.V1;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.List;
import javax.validation.Valid;
import lombok.val;
import org.ignast.stockinvesting.quotes.domain.Companies;
import org.ignast.stockinvesting.quotes.domain.Company;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockExchanges;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final Companies companies;

    private final StockExchanges stockExchanges;

    public CompanyController(final Companies companies, final StockExchanges stockExchanges) {
        this.companies = companies;
        this.stockExchanges = stockExchanges;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @PutMapping(consumes = V1, produces = V1)
    public EntityModel<CompanyDTO> createCompany(@Valid @RequestBody final CompanyDTO companyDTO) {
        final val externalId = companyDTO.getId();
        companies.create(mapFromDto(companyDTO));
        final val selfLink = linkTo(CompanyController.class).slash(externalId).withSelfRel();
        return EntityModel.of(companyDTO, selfLink);
    }

    @GetMapping(value = "/{id}", produces = V1)
    public EntityModel<CompanyDTO> retrieveCompanyById(@PathVariable final int id) {
        final val company = companies.findByExternalId(new CompanyExternalId(id));
        final val selfLink = linkTo(CompanyController.class).slash(id).withSelfRel();

        return EntityModel.of(mapToDto(company), selfLink);
    }

    private Company mapFromDto(final CompanyDTO companyDTO) {
        return companyDTO
            .getListings()
            .stream()
            .findFirst()
            .map(l -> {
                final val externalId = new CompanyExternalId(companyDTO.getId());
                final val name = new CompanyName(companyDTO.getName());
                final val symbol = new StockSymbol(l.getStockSymbol());
                final val stockExchange = stockExchanges.getFor(
                    new MarketIdentifierCode(l.getMarketIdentifier())
                );
                return Company.create(externalId, name, symbol, stockExchange);
            })
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Company to be created was expected to have one listing, but zero was found"
                )
            );
    }

    private CompanyDTO mapToDto(final Company company) {
        final val listingDto = new ListingDTO(
            company.getStockExchange().getMarketIdentifierCode().get(),
            company.getStockSymbol().get()
        );
        return new CompanyDTO(company.getExternalId().get(), company.getName().get(), List.of(listingDto));
    }
}
