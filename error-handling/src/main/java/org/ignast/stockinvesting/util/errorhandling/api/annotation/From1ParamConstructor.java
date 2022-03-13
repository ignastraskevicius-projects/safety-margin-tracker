package org.ignast.stockinvesting.util.errorhandling.api.annotation;

public interface From1ParamConstructor<T> {
    Object construct(T arg);
}