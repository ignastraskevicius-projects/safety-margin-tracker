package org.ignast.stockinvesting.util.errorhandling.api.annotation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor(staticName = "supporting")
public class SupportedTypes {
    @NonNull
    private final Map<Class<?>, FromStringConstructor> supportedObjects;
}