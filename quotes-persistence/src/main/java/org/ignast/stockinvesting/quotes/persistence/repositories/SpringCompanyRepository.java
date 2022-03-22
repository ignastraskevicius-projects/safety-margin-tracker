package org.ignast.stockinvesting.quotes.persistence.repositories;

import java.util.Optional;
import org.ignast.stockinvesting.quotes.domain.Company;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.springframework.data.repository.Repository;

public interface SpringCompanyRepository extends Repository<Company, Integer> {
    public void save(Company company);

    public Optional<Company> findByExternalId(CompanyExternalId externalId);
}
