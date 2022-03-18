package org.ignast.stockinvesting.estimates.service;

import java.util.Optional;
import org.ignast.stockinvesting.estimates.domain.Company;
import org.springframework.data.repository.Repository;

public interface CompanyRepository extends Repository<Company, String> {
    public Company save(Company company);

    public Optional<Company> findById(String id);
}
