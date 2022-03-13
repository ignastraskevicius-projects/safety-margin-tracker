package org.ignast.stockinvesting.quotes;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CompaniesTest {

    private CompanyRepository repository = mock(CompanyRepository.class);

    @Test
    public void shouldNotBeCreatedWithNullRepository() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Companies(null));
    }

    @Test
    public void createdCompanyShouldBePersisted() {
        val companies = new Companies(repository);
        val company = mock(Company.class);

        companies.create(company);

        verify(repository).save(company);
    }

    @Test
    public void createdFindCreatedCompanyByExternalId() {
        val companies = new Companies(repository);
        val company = mock(Company.class);

        companies.create(company);

        verify(repository).save(company);
    }
}