package org.ignast.stockinvesting.quotes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Companies {
    @NonNull private CompanyRepository repository;

    public void create(Company company) {
        repository.save(company);
    }
}
