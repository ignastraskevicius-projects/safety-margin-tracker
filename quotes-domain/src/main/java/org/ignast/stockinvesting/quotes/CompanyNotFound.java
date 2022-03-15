package org.ignast.stockinvesting.quotes;

import static java.lang.String.format;

public class CompanyNotFound extends ApplicationException {
    public CompanyNotFound(PositiveNumber externalId) {
        super(format("Company with id '%d' was not found", externalId.get()));
    }
}
