package org.ignast.stockinvesting.api.controller.errorhandler;

import org.ignast.stockinvesting.api.controller.errorhandler.annotations.CurrencyCode;

import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationStubs {
    static Override javaLangOverride() {
        Override annotation = new Override() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Override.class;
            }
        };
        assertThat(annotation.annotationType() == Override.class);
        return annotation;
    }

    static SuppressWarnings javaLangSuppressWarning() {
        SuppressWarnings annotation = new SuppressWarnings() {

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

    static NotNull javaxValidationNotNull() {
        NotNull annotation = new NotNull() {

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

    static Pattern javaxValidationPattern() {
        Pattern annotation = new Pattern() {

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

    static CurrencyCode javaxValidationCurrencyCode() {
        CurrencyCode annotation = new CurrencyCode() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return CurrencyCode.class;
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
        };
        assertThat(annotation.annotationType()).isEqualTo(CurrencyCode.class);
        return annotation;
    }

    static Size javaxValidationSize() {
        Size annotation = new Size() {

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
