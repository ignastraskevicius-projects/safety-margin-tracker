package org.ignast.stockinvesting.quotes.domain;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import lombok.val;
import org.junit.jupiter.api.Test;

public final class CompaniesTest {

    private final CompanyRepository repository = mock(CompanyRepository.class);

    private final Companies companies = new Companies(repository);

    @Test
    public void shouldNotBeCreatedWithNullRepository() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Companies(null));
    }

    @Test
    public void shouldNotCreateNullCompanies() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> companies.create(null));
    }

    @Test
    public void createdCompanyShouldBePersisted() {
        final val company = mock(Company.class);

        companies.create(company);

        verify(repository).save(company);
    }

    @Test
    public void shouldFindCreatedCompanyByExternalId() {
        final val company = mock(Company.class);
        final val externalId = new CompanyExternalId(5);
        when(repository.findByExternalId(externalId)).thenReturn(of(company));

        final val retrievedCompany = companies.findByExternalId(externalId);

        assertThat(retrievedCompany).isSameAs(company);
    }

    @Test
    public void shouldNotFindNonexistentCompanyByExternalId() {
        final val externalId = new CompanyExternalId(5);
        when(repository.findByExternalId(externalId)).thenReturn(empty());

        assertThatExceptionOfType(CompanyNotFound.class)
            .isThrownBy(() -> companies.findByExternalId(externalId))
            .isInstanceOf(ApplicationException.class)
            .withMessage("Company with id '5' was not found");
    }

    @Test
    public void shouldNotRetrieveCompaniesByNullId() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> companies.findByExternalId(null));
    }
}
