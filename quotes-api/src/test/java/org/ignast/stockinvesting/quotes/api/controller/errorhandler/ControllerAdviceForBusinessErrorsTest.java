package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.ignast.stockinvesting.quotes.domain.CompanyNotFound;
import org.ignast.stockinvesting.quotes.domain.CompanyRepository.CompanyAlreadyExists;
import org.ignast.stockinvesting.quotes.domain.CompanyRepository.ListingAlreadyExists;
import org.ignast.stockinvesting.quotes.domain.StockSymbolNotSupportedInThisMarket;
import org.junit.jupiter.api.Test;

public final class ControllerAdviceForBusinessErrorsTest {

    private final ControllerAdviceForBusinessErrors handler = new ControllerAdviceForBusinessErrors();

    @Test
    public void shouldHandleSymbolNotSupported() {
        assertThat(handler.handleCompanyNotFound(mock(CompanyNotFound.class)).getErrorName()).isNull();
    }

    @Test
    public void shouldHandleSymbolNotSupportedInMarket() {
        assertThat(
            handler
                .handleSymbolNotSupportedInMarket(mock(StockSymbolNotSupportedInThisMarket.class))
                .getErrorName()
        )
            .isEqualTo("stockSymbolNotSupportedInThisMarket");
    }

    @Test
    public void shouldHandleCompanyAlreadyExists() {
        assertThat(handler.handleCompanyAlreadyExists(mock(CompanyAlreadyExists.class)).getErrorName())
            .isEqualTo("companyAlreadyExists");
    }

    @Test
    public void shouldHandleListingAlreadyExists() {
        assertThat(handler.handleListingAlreadyExists(mock(ListingAlreadyExists.class)).getErrorName())
            .isEqualTo("listingAlreadyExists");
    }
}
