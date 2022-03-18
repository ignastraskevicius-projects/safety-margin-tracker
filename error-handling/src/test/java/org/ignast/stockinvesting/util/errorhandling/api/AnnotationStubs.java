package org.ignast.stockinvesting.util.errorhandling.api;

import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;

import java.lang.annotation.Annotation;

import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.assertj.core.api.Assertions.assertThat;

public final class AnnotationStubs {
    private AnnotationStubs() {

    }

    static Override javaLangOverride() {
        final val annotation = new Override() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Override.class;
            }
        };
        assertThat(annotation.annotationType() == Override.class);
        return annotation;
    }

    static SuppressWarnings javaLangSuppressWarning() {
        final val annotation = new SuppressWarnings() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return SuppressWarnings.class;
            }

            @Override
            public String[] value() {
                return new String[0];
            }
        };
        assertThat(annotation.annotationType()).isEqualTo(SuppressWarnings.class);
        return annotation;
    }

    @SuppressWarnings("checkstyle:anoninnerlength")
    static NotNull javaxValidationNotNull() {
        final val annotation = new NotNull() {

            @Override
            public String message() {
                return null;
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return NotNull.class;
            }
        };
        assertThat(annotation.annotationType() == NotNull.class);
        return annotation;
    }

    @SuppressWarnings("checkstyle:anoninnerlength")
    static Pattern javaxValidationPattern() {
        final val annotation = new Pattern() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Pattern.class;
            }

            @Override
            public String regexp() {
                return null;
            }

            @Override
            public Flag[] flags() {
                return new Flag[0];
            }

            @Override
            public String message() {
                return null;
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }
        };
        assertThat(annotation.annotationType()).isEqualTo(Pattern.class);
        return annotation;
    }

    @SuppressWarnings("checkstyle:anoninnerlength")
    static DomainClassConstraint javaxValidationDomainClassConstraint() {
        final val annotation = new DomainClassConstraint() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return DomainClassConstraint.class;
            }

            @Override
            public String message() {
                return null;
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<?>[] payload() {
                return new Class[0];
            }

            @Override
            public Class<?> domainClass() {
                return null;
            }
        };
        assertThat(annotation.annotationType()).isEqualTo(DomainClassConstraint.class);
        return annotation;
    }

    @SuppressWarnings("checkstyle:anoninnerlength")
    static Size javaxValidationSize() {
        final val annotation = new Size() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Size.class;
            }

            @Override
            public String message() {
                return null;
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public int min() {
                return 0;
            }

            @Override
            public int max() {
                return 0;
            }
        };
        assertThat(annotation.annotationType() == Size.class);
        return annotation;
    }
}
