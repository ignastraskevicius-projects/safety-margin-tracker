package org.ignast.stockinvesting.util.errorhandling.api.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.StandardErrorDTO;
import org.springframework.web.servlet.HandlerInterceptor;

public final class MediaTypeCheckerInterceptor implements HandlerInterceptor {

    private static final boolean PASS_ONTO_NEXT_HANDLER = true;

    private static final int NOT_ACCEPTABLE = 406;

    private final ObjectMapper mapper;

    public MediaTypeCheckerInterceptor(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean preHandle(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Object h
    ) throws IOException {
        if (
            isGETrequestMissingMediaTypeAsPerExplicitApiVersionRequirementNotToBreakClientsOnUpgrades(request)
        ) {
            response.setStatus(NOT_ACCEPTABLE);
            final val error = mapper.writeValueAsString(StandardErrorDTO.createForNotAcceptableNoHeader());
            response.getWriter().write(error);
            response.getWriter().flush();
            return !PASS_ONTO_NEXT_HANDLER;
        } else {
            return PASS_ONTO_NEXT_HANDLER;
        }
    }

    private boolean isGETrequestMissingMediaTypeAsPerExplicitApiVersionRequirementNotToBreakClientsOnUpgrades(
        final HttpServletRequest request
    ) {
        return isGET(request) && !containsAcceptHeader(request);
    }

    private boolean isGET(final HttpServletRequest request) {
        return "GET".equals(request.getMethod());
    }

    private boolean containsAcceptHeader(final HttpServletRequest request) {
        return Collections
            .list(request.getHeaderNames())
            .stream()
            .map(String::toLowerCase)
            .collect(Collectors.toUnmodifiableList())
            .contains("accept");
    }
}
