package org.ignast.stockinvesting.quotes.util.test.api.traversor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static org.ignast.stockinvesting.quotes.util.test.api.traversor.HateoasLink.anyLink;
import static org.ignast.stockinvesting.quotes.util.test.api.traversor.HateoasLink.link;

public class HateoasLink {
    public static String link(String rel, String href) {
        return format("{\"_links\":{\"%s\":{\"href\":\"%s\"}}}", rel, href);
    }

    public static String anyLink() {
        return "{\"_links\":{\"any\":{\"href\":\"any\"}}}";
    }
}

class HateoasLinkTest {
    @Test
    public void shouldCreateLink() {
        Assertions.assertThat(link("company", "companyUri")).isEqualTo("{\"_links\":{\"company\":{\"href\":\"companyUri\"}}}");
    }

    @Test
    public void shouldCreateAnyLink() {
        Assertions.assertThat(anyLink()).isEqualTo("{\"_links\":{\"any\":{\"href\":\"any\"}}}");
    }
}