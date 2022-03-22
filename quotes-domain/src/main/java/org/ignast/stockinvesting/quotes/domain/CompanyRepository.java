package org.ignast.stockinvesting.quotes.domain;

import static java.lang.String.format;

import java.util.Optional;

public interface CompanyRepository {
    public void save(Company company);

    public Optional<Company> findByExternalId(CompanyExternalId externalId);

    public static final class CompanyAlreadyExists extends RuntimeException {

        public CompanyAlreadyExists(final CompanyExternalId externalId, final RuntimeException e) {
            super(format("Company with external id '%d' already exists", externalId.get()), e);
        }
    }

    public static final class ListingAlreadyExists extends RuntimeException {

        public ListingAlreadyExists(
            final StockSymbol symbol,
            final MarketIdentifierCode marketIdentifier,
            final RuntimeException e
        ) {
            super(
                format(
                    "Company with stock symbol '%s' in the market identified by '%s' code already exists",
                    symbol.get(),
                    marketIdentifier.get()
                ),
                e
            );
        }
    }

    public static final class CompanyCreationFailed extends RuntimeException {

        public CompanyCreationFailed(final RuntimeException e) {
            super(e);
        }
    }
}
