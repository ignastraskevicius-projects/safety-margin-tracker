package org.ignast.stockinvesting.quotes.domain;

import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface CompanyRepository extends Repository<Company, Integer> {
    public Company save(Company company);

    public Optional<Company> findByExternalId(CompanyExternalId externalId);
}
