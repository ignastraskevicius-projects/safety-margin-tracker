package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.ReferenceMock.toField;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.util.List;
import lombok.val;
import org.ignast.stockinvesting.utiltest.MockitoUtils;
import org.junit.jupiter.api.Test;

final class ReferenceMock {

    private ReferenceMock() {}

    public static JsonMappingException.Reference toField(final Object sourceObject, final String field) {
        return MockitoUtils.mock(
            JsonMappingException.Reference.class,
            r -> {
                when(r.getFrom()).thenReturn(sourceObject);
                when(r.getFieldName()).thenReturn(field);
            }
        );
    }

    public static JsonMappingException.Reference toIndex(final Object sourceObject, final int index) {
        return MockitoUtils.mock(
            JsonMappingException.Reference.class,
            r -> {
                when(r.getFrom()).thenReturn(sourceObject);
                when(r.getIndex()).thenReturn(index);
            }
        );
    }
}

final class ReferenceMockTest {

    @Test
    public void shouldCreateFieldPath() {
        final val source = new City();
        final val fieldName = "population";
        final val reference = toField(source, fieldName);

        assertThat(reference.getFrom()).isSameAs(source);
        assertThat(reference.getFieldName()).isEqualTo(fieldName);
    }

    @Test
    public void shouldCreateIndexPath() {
        final val source = List.of();
        final val index = 3;
        final val reference = ReferenceMock.toIndex(source, index);

        assertThat(reference.getFrom()).isSameAs(source);
        assertThat(reference.getIndex()).isEqualTo(index);
    }

    private static final class City {}
}
