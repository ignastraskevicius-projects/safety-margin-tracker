package org.ignast.stockinvesting.testutil.api.traversor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.ignast.stockinvesting.testutil.api.traversor.HateoasLink.anyLink;
import static org.ignast.stockinvesting.testutil.api.traversor.HateoasLink.link;

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