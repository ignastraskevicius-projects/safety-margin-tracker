package org.ignast.stockinvesting.quotes.api.controller;

import lombok.val;
import org.ignast.stockinvesting.quotes.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

class CompanyControllerTest {

    private StockExchanges stockExchanges = mock(StockExchanges.class);

    private Companies companies = mock(Companies.class);

    private CompanyController controller = new CompanyController(companies, stockExchanges);

    @Test
    public void shouldCreateCompany() {
        val stockExchange = mock(StockExchange.class);
        when(stockExchanges.getFor(new MarketIdentifierCode("XNYS"))).thenReturn(stockExchange);
        val id = "48f70e54-c66a-4c87-9f02-81bdc84c63b3";
        val company = new CompanyDTO(id, "Microsoft", asList(new ListingDTO("XNYS", "MSFT")));
        val captor = ArgumentCaptor.forClass(Company.class);

        controller.createCompany(company);

        verify(companies).create(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new Company(UUID.fromString(id), new CompanyName("Microsoft"), new StockSymbol("MSFT"), stockExchange));
    }

    @Test
    public void shouldRejectDtoWithoutListings() {
        val id = "48f70e54-c66a-4c87-9f02-81bdc84c63b3";
        val company = new CompanyDTO(id, "Microsoft", asList());

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> controller.createCompany(company)).withMessage("Company to be created was expected to have one listing, but zero was found");
    }

}