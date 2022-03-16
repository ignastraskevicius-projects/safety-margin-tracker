package org.ignast.stockinvesting.quotes.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CompanyIdTest {
    @Test
    public void shouldNotBeNull() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> CompanyId.toUUID(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "abcdefab-cdef-abcd-efab-cdefabcdefab",
                             "12345678-9012-3456-7890-123456789012",
                             "ABCDEFAB-CDEF-ABCD-EFAB-CDEFACBDEFAB",
                             "A4C7A6dF-0950-431C-9315-ccf9a2ef1a90"})
    public void shouldPreserveInitialValue(String uuid) {
        assertThat(CompanyId.toUUID(uuid).toString()).isEqualTo(uuid.toLowerCase());
    }

    @Test
    public void shouldRejectNon36Character() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> CompanyId.toUUID("19c56404-73c6-4cd1-96a4-aae7962b6435a"))
                .withMessage("Must consist of 36 characters");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> CompanyId.toUUID("19c56404-73c6-4cd1-96a4-aae7962b643"))
                .withMessage("Must consist of 36 characters");
    }

    @ParameterizedTest
    @ValueSource(strings = { "ÑÑÑÑÑÑÑÑ-4455-6677-8899-abcdefabcdef",
                             "00112233-!!!!-6677-8899-abcdefabcdef",
                             "00112233-4455-6677-8899-GGGGGGGGGGGG",
                             "00112233-4455-6677-8899-gggggggggggg",
                             "00112233-4455-6677-8899-abcdefabcd  "})
    public void shouldRejectNonNumericAndNonABCDEandFCharacters(String id) {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> CompanyId.toUUID(id))
                .withMessage("Must consist of hyphens (-) and a,b,c,d,e,f and numeric characters only");
    }

    @ParameterizedTest
    @ValueSource(strings = { "19C56-04-73c6-4cd1-96a43aae7962b-643", "19c5640473c64cd196a4aae7962b64356435" })
    public void shouldRejectInvalidUUID(String invalidUuid) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> CompanyId.toUUID(invalidUuid)).withMessage(
                        "Must be a valid UUID");
    }
}