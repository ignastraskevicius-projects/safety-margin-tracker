package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.val;
import org.ignast.stockinvesting.quotes.domain.CompanyNotFound;
import org.ignast.stockinvesting.quotes.domain.CompanyRepository.CompanyAlreadyExists;
import org.ignast.stockinvesting.quotes.domain.CompanyRepository.ListingAlreadyExists;
import org.ignast.stockinvesting.quotes.domain.StockExchangeNotSupported;
import org.ignast.stockinvesting.quotes.domain.StockSymbolNotSupportedInThisMarket;
import org.junit.jupiter.api.Test;

public final class ControllerAdviceForBusinessErrorsTest {

    private final ControllerAdviceForBusinessErrors handler = new ControllerAdviceForBusinessErrors();

    @Test
    public void shouldHandleCompanyNotFound() {
        final val error = handler.handleCompanyNotFound(mock(CompanyNotFound.class));

        assertThat(error.getErrorName()).isNull();
        assertThat(error.getHttpStatus()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    public void shouldHandleSymbolNotSupportedInMarket() {
        final val error = handler.handleSymbolNotSupportedInMarket(
            mock(StockSymbolNotSupportedInThisMarket.class)
        );

        assertThat(error.getErrorName()).isEqualTo("stockSymbolNotSupportedInThisMarket");
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void shouldHandleMarketNotSupported() {
        final val error = handler.handleMarketNotSupported(mock(StockExchangeNotSupported.class));

        assertThat(error.getErrorName()).isEqualTo("marketNotSupported");
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void shouldHandleCompanyAlreadyExists() {
        final val error = handler.handleCompanyAlreadyExists(mock(CompanyAlreadyExists.class));

        assertThat(error.getErrorName()).isEqualTo("companyAlreadyExists");
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void shouldHandleListingAlreadyExists() {
        final val error = handler.handleListingAlreadyExists(mock(ListingAlreadyExists.class));
        assertThat(error.getErrorName()).isEqualTo("listingAlreadyExists");
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }
}
