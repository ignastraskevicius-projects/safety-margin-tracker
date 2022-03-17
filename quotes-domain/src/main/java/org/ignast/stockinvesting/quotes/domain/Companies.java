package org.ignast.stockinvesting.quotes.domain;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Companies {
    @NonNull private CompanyRepository repository;

    public void create(@NonNull Company company) {
        repository.save(company);
    }

    public Company findByExternalId(@NonNull CompanyExternalId id) {
        return repository.findByExternalId(id).orElseThrow(() -> new CompanyNotFound(id));
    }
}