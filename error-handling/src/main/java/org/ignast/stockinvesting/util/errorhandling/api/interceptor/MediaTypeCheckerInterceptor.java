package org.ignast.stockinvesting.util.errorhandling.api.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class MediaTypeCheckerInterceptor implements HandlerInterceptor {
    private final static boolean PASS_ONTO_NEXT_HANDLER = true;

    private final static int NOT_ACCEPTABLE = 406;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        if (isGETrequestMissingMediaTypeAsPerExplicitApiVersionRequirementNotToBreakClientsOnUpgrades(request)) {
            response.setStatus(NOT_ACCEPTABLE);
            return !PASS_ONTO_NEXT_HANDLER;
        } else {
            return PASS_ONTO_NEXT_HANDLER;
        }
    }

    private boolean isGETrequestMissingMediaTypeAsPerExplicitApiVersionRequirementNotToBreakClientsOnUpgrades(
            final HttpServletRequest request) {
        return isGET(request) && !containsAcceptHeader(request);
    }

    private boolean isGET(final HttpServletRequest request) {
        return "GET".equals(request.getMethod());
    }

    private boolean containsAcceptHeader(final HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream().map(String::toLowerCase)
                .collect(Collectors.toUnmodifiableList()).contains("accept");
    }
}
