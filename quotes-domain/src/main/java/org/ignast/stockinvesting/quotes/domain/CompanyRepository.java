package org.ignast.stockinvesting.quotes.domain;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface CompanyRepository extends Repository<Company, Integer> {
    public Company save(Company company);

    public Optional<Company> findByExternalId(CompanyExternalId externalId);
}
