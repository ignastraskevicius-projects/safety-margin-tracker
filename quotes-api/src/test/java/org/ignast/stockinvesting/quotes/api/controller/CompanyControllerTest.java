package org.ignast.stockinvesting.quotes.api.controller;

import lombok.val;
import org.ignast.stockinvesting.quotes.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.api.controller.DomainFactoryForTests.amazon;
import static org.ignast.stockinvesting.quotes.api.controller.TestDtos.amazonDto;
import static org.mockito.Mockito.*;

class CompanyControllerTest {

    private StockExchanges stockExchanges = mock(StockExchanges.class);

    private Companies companies = mock(Companies.class);

    private CompanyController controller = new CompanyController(companies, stockExchanges);

    @Test
    public void shouldCreateCompany() {
        val stockExchange = mock(StockExchange.class);
        when(stockExchanges.getFor(new MarketIdentifierCode("XNAS"))).thenReturn(stockExchange);
        val companyDto = new CompanyDTO(5, "Microsoft", asList(new ListingDTO("XNAS", "MSFT")));
        val captor = ArgumentCaptor.forClass(Company.class);

        val createdCompanyDto = controller.createCompany(companyDto);

        verify(companies).create(captor.capture());
        val company = captor.getValue();
        assertThat(company.getExternalId()).isEqualTo(new CompanyExternalId(5));
        assertThat(company.getName()).isEqualTo(new CompanyName("Microsoft"));
        assertThat(company.getStockSymbol()).isEqualTo(new StockSymbol("MSFT"));
        assertThat(company.getStockExchange()).isEqualTo(stockExchange);

        assertThat(createdCompanyDto.getContent()).isEqualTo(companyDto);
        assertThat(createdCompanyDto.getLink("self").isPresent());
        createdCompanyDto.getLink("self").stream().forEach(c ->
                assertThat(c.getHref()).endsWith("/companies/5"));
    }

    @Test
    public void companyShouldLinkToItself() {
        when(stockExchanges.getFor(new MarketIdentifierCode("XNAS"))).thenReturn(mock(StockExchange.class));
        val companyDto = new CompanyDTO(5, "Microsoft", asList(new ListingDTO("XNAS", "MSFT")));
        val captor = ArgumentCaptor.forClass(Company.class);

        val createdCompanyDto = controller.createCompany(companyDto);

        assertThat(createdCompanyDto.getContent()).isEqualTo(companyDto);
        assertThat(createdCompanyDto.getRequiredLink("self").getHref()).endsWith(format("/companies/%d", 5));
    }

    @Test
    public void shouldRejectDtoWithoutListings() {
        val company = new CompanyDTO(1, "Microsoft", asList());

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> controller.createCompany(company)).withMessage("Company to be created was expected to have one listing, but zero was found");
    }

    @Test
    public void shouldRetrieveCompany() {
        val amazonExternalId = amazon().getExternalId().get();
        when(companies.findByExternalId(new CompanyExternalId(amazonExternalId))).thenReturn(amazon());

        val retrievedCompany = controller.retrieveCompanyById(amazonExternalId);

        assertThat(retrievedCompany.getContent()).isEqualTo(amazonDto());
    }

    @Test
    public void retrievedCompanyShouldLinkToItself() {
        val amazonExternalId = amazon().getExternalId().get();
        when(companies.findByExternalId(new CompanyExternalId(amazonExternalId))).thenReturn(amazon());

        val retrievedCompany = controller.retrieveCompanyById(amazonExternalId);

        assertThat(retrievedCompany.getRequiredLink("self").getHref()).endsWith(format("/companies/%d", amazonExternalId));
    }
}