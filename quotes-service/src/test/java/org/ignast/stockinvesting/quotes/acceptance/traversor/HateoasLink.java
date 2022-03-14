package org.ignast.stockinvesting.quotes.acceptance.traversor;

import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.quotes.acceptance.traversor.HateoasLink.anyLink;
import static org.ignast.stockinvesting.quotes.acceptance.traversor.HateoasLink.link;

class HateoasLink {
    static String link(String rel, String href) {
        return format("{\"_links\":{\"%s\":{\"href\":\"%s\"}}}", rel, href);
    }

    static String anyLink() {
        return "{\"_links\":{\"any\":{\"href\":\"any\"}}}";
    }
}

class HateoasLinkTest {
    @Test
    public void shouldCreateLink() {
        assertThat(link("company", "companyUri")).isEqualTo("{\"_links\":{\"company\":{\"href\":\"companyUri\"}}}");
    }

    @Test
    public void shouldCreateAnyLink() {
        assertThat(anyLink()).isEqualTo("{\"_links\":{\"any\":{\"href\":\"any\"}}}");
    }
}