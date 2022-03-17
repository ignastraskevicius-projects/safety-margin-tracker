package org.ignast.stockinvesting.testutil.api.traversor;

import lombok.NonNull;
import lombok.val;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public final class HateoasTraversor {
    private final MediaType appMediaType;

    private final Hop.Factory hopFactory;

    private final List<Hop.TraversableHop> hops;

    private final String rootUri;

    private HateoasTraversor(MediaType appMediaType, Hop.Factory hopFactory, String rootUri, List<Hop.TraversableHop> hops) {
        this.appMediaType = appMediaType;
        this.hopFactory = hopFactory;
        this.rootUri = rootUri;
        this.hops = hops;
    }

    public HateoasTraversor hop(Function<Hop.Factory, Hop.TraversableHop> constructHop) {
        Hop.TraversableHop hop = constructHop.apply(hopFactory);
        return new HateoasTraversor(appMediaType, hopFactory, rootUri, concat(hops.stream(), of(hop)).collect(toUnmodifiableList()));
    }

    public ResponseEntity<String> perform() {
        val fakeLinkToRoot = ResponseEntity.status(HttpStatus.OK).contentType(appMediaType).body(HateoasLink.link("root", rootUri));
        val rootHop = hopFactory.get("root");
        return concat(of(rootHop), hops.stream()).reduce(fakeLinkToRoot, (r, h) -> h.traverse(r), combinerUnsupported());
    }

    private BinaryOperator<ResponseEntity<String>> combinerUnsupported() {
        return (a, b) -> {
            throw new IllegalArgumentException("combinations are not supported");
        };
    }

    @Service
    public static final class Factory {
        private final Hop.Factory hopFactory;

        private final MediaType appMediaType;

        public Factory(RestTemplateBuilder builder, MediaType appMediaType) {
            this.appMediaType = appMediaType;
            val restTemplate = builder.errorHandler(new NoSpecialHandling()).build();
            hopFactory = new Hop.Factory(appMediaType, restTemplate, new HrefExtractor(appMediaType));
        }

        public HateoasTraversor startAt(@NonNull String rootUri) {
            return new HateoasTraversor(appMediaType, hopFactory, rootUri, emptyList());
        }

        private class NoSpecialHandling implements ResponseErrorHandler {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        }
    }
}
