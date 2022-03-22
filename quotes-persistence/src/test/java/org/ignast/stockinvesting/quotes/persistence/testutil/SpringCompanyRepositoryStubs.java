package org.ignast.stockinvesting.quotes.persistence.testutil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.persistence.testutil.SpringCompanyRepositoryStubs.stubDuplicateSavingsOn;
import static org.ignast.stockinvesting.quotes.persistence.testutil.SpringCompanyRepositoryStubs.stubRetrieving;
import static org.ignast.stockinvesting.quotes.persistence.testutil.SpringCompanyRepositoryStubs.stubSavingsWithDuplicateListingOn;
import static org.ignast.stockinvesting.quotes.persistence.testutil.SpringCompanyRepositoryStubs.stubThrowsOnSaving;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import lombok.val;
import org.ignast.stockinvesting.quotes.domain.Company;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.persistence.repositories.SpringCompanyRepository;
import org.ignast.stockinvesting.quotes.persistence.testutil.SpringCompanyRepositoryStubs.Databases;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

public final class SpringCompanyRepositoryStubs implements SpringCompanyRepository {

    private final SpringCompanyRepository repository = mock(SpringCompanyRepository.class);

    private SpringCompanyRepositoryStubs() {}

    public static SpringCompanyRepositoryStubs stubRetrieving(final Company company) {
        final val stub = new SpringCompanyRepositoryStubs();
        when(stub.repository.findByExternalId(any())).thenReturn(Optional.of(company));
        return stub;
    }

    public static SpringCompanyRepositoryStubs stubThrowsOnSaving(final DataIntegrityViolationException e) {
        final val stub = new SpringCompanyRepositoryStubs();
        doThrow(e).when(stub.repository).save(any());
        return stub;
    }

    public static SpringCompanyRepositoryStubs stubSavingsWithDuplicateListingOn(final Choice choice) {
        final val message = choice.chooseFrom(new Databases()).duplicateListingAttemptedMessage();
        final val stub = new SpringCompanyRepositoryStubs();
        doThrow(new DataIntegrityViolationException(message)).when(stub.repository).save(any());
        return stub;
    }

    public static SpringCompanyRepositoryStubs stubDuplicateSavingsOn(final Choice choice) {
        final val message = choice.chooseFrom(new Databases()).duplicateCompanyMessage();
        final val stub = new SpringCompanyRepositoryStubs();
        doThrow(new DataIntegrityViolationException(message)).when(stub.repository).save(any());
        return stub;
    }

    @Override
    public void save(final Company company) {
        repository.save(company);
    }

    @Override
    public Optional<Company> findByExternalId(final CompanyExternalId externalId) {
        return repository.findByExternalId(externalId);
    }

    public static final class Databases {

        public Database h2() {
            return new H2();
        }

        public Database mysql() {
            return new MySQL();
        }
    }

    public static interface Choice {
        public Database chooseFrom(Databases options);
    }

    public static interface Database {
        public String duplicateListingAttemptedMessage();

        public String duplicateCompanyMessage();
    }

    private static final class H2 implements Database {

        @Override
        public String duplicateListingAttemptedMessage() {
            return "H2 will throw constraint name 'unique_listing' in lowercase";
        }

        @Override
        public String duplicateCompanyMessage() {
            return "H2 will mention constraint name 'unique_external_id' in lowercase";
        }
    }

    private static final class MySQL implements Database {

        @Override
        public String duplicateListingAttemptedMessage() {
            return "Mysql will mention constraint name 'UNIQUE_LISTING' in uppercase";
        }

        @Override
        public String duplicateCompanyMessage() {
            return "Mysql will mention constraint name 'UNIQUE_EXTERNAL_ID' in uppercase";
        }
    }
}

final class SpringCompanyRepositoryStubsTest {

    @Test
    public void shouldStubRetrievingCompany() {
        final val company = mock(Company.class);
        final val anyExternalId = mock(CompanyExternalId.class);

        assertThat(stubRetrieving(company).findByExternalId(anyExternalId).get()).isSameAs(company);
    }

    @Test
    public void shouldStubSavingsWithDuplicateListingOnH2() {
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> stubSavingsWithDuplicateListingOn(Databases::h2).save(mock(Company.class)))
            .withMessageContaining("unique_listing");
    }

    @Test
    public void shouldStubSavingsWithDuplicateListingsOnMysql() {
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> stubSavingsWithDuplicateListingOn(Databases::mysql).save(mock(Company.class)))
            .withMessageContaining("UNIQUE_LISTING");
    }

    @Test
    public void shouldStubDuplicateSavingsOnH2() {
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> stubDuplicateSavingsOn(Databases::h2).save(mock(Company.class)))
            .withMessageContaining("unique_external_id");
    }

    @Test
    public void shouldStubDuplicateSavingsOnMysql() {
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> stubDuplicateSavingsOn(Databases::mysql).save(mock(Company.class)))
            .withMessageContaining("UNIQUE_EXTERNAL_ID");
    }

    @Test
    public void shouldStubCustomError() {
        final val customException = new DataIntegrityViolationException("customStub");
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> stubThrowsOnSaving(customException).save(mock(Company.class)))
            .withMessage("customStub");
    }
}
