package org.ignast.stockinvesting.quotes.domain;

import static java.lang.String.format;

public class CompanyNotFound extends ApplicationException {

    public CompanyNotFound(final CompanyExternalId externalId) {
        super(format("Company with id '%d' was not found", externalId.get()));
    }
}
