package org.ignast.stockinvesting.quotes;

import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends Repository<Company, Integer> {
    Company save(Company company);

    Optional<Company> findByExternalId(PositiveNumber externalId);
}
