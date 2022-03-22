package org.ignast.stockinvesting.quotes.persistence.repositories;

import static java.util.Objects.isNull;

import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.ignast.stockinvesting.quotes.domain.Company;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExceptionMappingCompanyRepository implements CompanyRepository {

    @NonNull
    private final SpringCompanyRepository underlyingRepository;

    @Override
    public void save(final Company company) {
        try {
            underlyingRepository.save(company);
        } catch (DataIntegrityViolationException e) {
            mapToBusinessError(company, e);
        }
    }

    private void mapToBusinessError(final Company company, final DataIntegrityViolationException e) {
        if (isNull(e.getMessage())) {
            throw new CompanyCreationFailed(e);
        } else if (e.getMessage().toLowerCase().contains("unique_listing")) {
            throw new ListingAlreadyExists(
                company.getStockSymbol(),
                company.getStockExchange().getMarketIdentifierCode(),
                e
            );
        } else if (e.getMessage().toLowerCase().contains("unique_external_id")) {
            throw new CompanyAlreadyExists(company.getExternalId(), e);
        } else {
            throw new CompanyCreationFailed(e);
        }
    }

    @Override
    public Optional<Company> findByExternalId(final CompanyExternalId externalId) {
        return underlyingRepository.findByExternalId(externalId);
    }
}
