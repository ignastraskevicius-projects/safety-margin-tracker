package org.ignast.stockinvesting.estimates.service;

import org.ignast.stockinvesting.estimates.domain.Company;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface CompanyRepository extends Repository<Company, String> {
    public Company save(Company company);

    public Optional<Company> findById(String id);
}
