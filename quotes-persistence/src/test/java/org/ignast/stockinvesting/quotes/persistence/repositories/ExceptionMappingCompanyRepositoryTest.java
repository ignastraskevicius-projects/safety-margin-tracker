package org.ignast.stockinvesting.quotes.persistence.repositories;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.persistence.testutil.DomainFactoryForTests.amazon;
import static org.ignast.stockinvesting.quotes.persistence.testutil.SpringCompanyRepositoryStubs.stubDuplicateSavingsOn;
import static org.ignast.stockinvesting.quotes.persistence.testutil.SpringCompanyRepositoryStubs.stubRetrieving;
import static org.ignast.stockinvesting.quotes.persistence.testutil.SpringCompanyRepositoryStubs.stubSavingsWithDuplicateListingOn;
import static org.ignast.stockinvesting.quotes.persistence.testutil.SpringCompanyRepositoryStubs.stubThrowsOnSaving;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;
import lombok.val;
import org.ignast.stockinvesting.quotes.domain.Company;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyRepository;
import org.ignast.stockinvesting.quotes.domain.CompanyRepository.CompanyAlreadyExists;
import org.ignast.stockinvesting.quotes.persistence.testutil.SpringCompanyRepositoryStubs.Choice;
import org.ignast.stockinvesting.quotes.persistence.testutil.SpringCompanyRepositoryStubs.Databases;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

final class ExceptionMappingCompanyRepositoryTest {

    @Test
    public void shouldSave() {
        final val underlyingRepository = mock(SpringCompanyRepository.class);
        final val repository = new ExceptionMappingCompanyRepository(underlyingRepository);

        repository.save(mock(Company.class));

        verify(underlyingRepository).save(any());
    }

    @Test
    public void shouldRetrieveCompany() {
        final val company = mock(Company.class);
        final val underlyingRepository = stubRetrieving(company);
        final val repository = new ExceptionMappingCompanyRepository(underlyingRepository);

        assertThat(repository.findByExternalId(mock(CompanyExternalId.class)).get()).isSameAs(company);
    }

    @Test
    public void shouldIndicateCompanyCreationFailedIfMessageWasNull() {
        final val springRepository = stubThrowsOnSaving(new DataIntegrityViolationException(null));
        final val repository = new ExceptionMappingCompanyRepository(springRepository);

        assertThatExceptionOfType(CompanyRepository.CompanyCreationFailed.class)
            .isThrownBy(() -> repository.save(amazon()))
            .withRootCauseInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void shouldIndicateCompanyCreationFailedIfMessageIsNotOneOfThoseExpected() {
        final val springRepository = stubThrowsOnSaving(
            new DataIntegrityViolationException("someOtherMessage")
        );
        final val repository = new ExceptionMappingCompanyRepository(springRepository);

        assertThatExceptionOfType(CompanyRepository.CompanyCreationFailed.class)
            .isThrownBy(() -> repository.save(amazon()))
            .withRootCauseInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void shouldIndicateCompanyAlreadyExists() {
        Stream
            .<Choice>of(Databases::h2, Databases::mysql)
            .forEach(db -> shouldIndicateCompanyAlreadyExistsOn(db));
    }

    private void shouldIndicateCompanyAlreadyExistsOn(final Choice database) {
        final val springRepository = stubDuplicateSavingsOn(database);
        final val repository = new ExceptionMappingCompanyRepository(springRepository);

        assertThatExceptionOfType(CompanyAlreadyExists.class)
            .isThrownBy(() -> repository.save(amazon()))
            .withMessage(
                format("Company with external id '%s' already exists", amazon().getExternalId().get())
            )
            .withRootCauseInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void shouldIndicateListingIsAttemptedToBeDuplicated() {
        Stream
            .<Choice>of(Databases::h2, Databases::mysql)
            .forEach(db -> shouldIndicateListingIsAttemptedToBeDuplicatedFor(db));
    }

    private void shouldIndicateListingIsAttemptedToBeDuplicatedFor(final Choice database) {
        final val springRepository = stubSavingsWithDuplicateListingOn(database);
        final val repository = new ExceptionMappingCompanyRepository(springRepository);

        assertThatExceptionOfType(CompanyRepository.ListingAlreadyExists.class)
            .isThrownBy(() -> repository.save(amazon()))
            .withMessage(
                "Company with stock symbol 'AMZN' in the market identified by 'XNAS' code already exists"
            )
            .withRootCauseInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void shouldCreateCompany() {
        final val repository = new ExceptionMappingCompanyRepository(mock(SpringCompanyRepository.class));

        repository.save(mock(Company.class));
    }
}
