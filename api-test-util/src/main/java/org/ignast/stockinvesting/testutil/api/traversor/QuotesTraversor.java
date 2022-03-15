package org.ignast.stockinvesting.testutil.api.traversor;

import lombok.NonNull;
import lombok.val;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class QuotesTraversor {
    private final Hop.Factory hopFactory;
    private final List<Hop.TraversableHop> hops;
    private final String rootUri;

    private QuotesTraversor(Hop.Factory hopFactory, String rootUri, List<Hop.TraversableHop> hops) {
        this.hopFactory = hopFactory;
        this.rootUri = rootUri;
        this.hops = hops;
    }

    public QuotesTraversor hop(Function<Hop.Factory, Hop.TraversableHop> constructHop) {
        Hop.TraversableHop hop = constructHop.apply(hopFactory);
        return new QuotesTraversor(hopFactory, rootUri, concat(hops.stream(), of(hop)).collect(toUnmodifiableList()));
    }

    public ResponseEntity<String> perform() {
        val fakeLinkToRoot = ResponseEntity.status(HttpStatus.OK).contentType(Hop.TraversableHop.APP_MEDIA_TYPE).body(HateoasLink.link("root", rootUri));
        val rootHop = hopFactory.get("root");
        return concat(of(rootHop), hops.stream()).reduce(fakeLinkToRoot, (r, h) -> h.traverse(r), combinerUnsupported());
    }

    private BinaryOperator<ResponseEntity<String>> combinerUnsupported() {
        return (a, b) -> {
            throw new IllegalArgumentException("combinations are not supported");
        };
    }

    @Service
    public static class Factory {
        private Hop.Factory hopFactory;

        public Factory(RestTemplateBuilder builder) {
            hopFactory = new Hop.Factory(builder.build(), new HrefExtractor());
        }

        public QuotesTraversor startAt(@NonNull String rootUri) {
            return new QuotesTraversor(hopFactory, rootUri, emptyList());
        }
    }
}
