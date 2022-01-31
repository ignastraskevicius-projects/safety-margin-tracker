package org.ignast.stockinvesting.api;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.stream.Collectors;

public class MediaTypeCheckerInterceptor implements HandlerInterceptor {
    private static boolean PASS_ONTO_NEXT_HANDLER = true;
    private static int NOT_ACCEPTABLE = 406;
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isGETrequestMissingMediaTypeAsPerExplicitApiVersionRequirementNotToBreakClientsOnUpgrades(request)) {
            response.setStatus(NOT_ACCEPTABLE);
            return !PASS_ONTO_NEXT_HANDLER;
        } else {
            return PASS_ONTO_NEXT_HANDLER;
        }
    }

    private boolean isGETrequestMissingMediaTypeAsPerExplicitApiVersionRequirementNotToBreakClientsOnUpgrades(HttpServletRequest request) {
        return isGET(request) && !containsAcceptHeader(request);
    }

    private boolean isGET(HttpServletRequest request) {
        return "GET".equals(request.getMethod());
    }

    private boolean containsAcceptHeader(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream().map(h -> h.toLowerCase()).collect(Collectors.toList()).contains("accept");
    }
}
