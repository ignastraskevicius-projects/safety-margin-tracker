package org.ignast.stockinvesting.estimates.service;

import org.ignast.stockinvesting.estimates.domain.Company;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends Repository<Company, String> {
    Company save(Company company);

    Optional<Company> findById(String id);
}
