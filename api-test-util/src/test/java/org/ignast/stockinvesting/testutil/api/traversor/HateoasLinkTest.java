package org.ignast.stockinvesting.testutil.api.traversor;

import static org.ignast.stockinvesting.testutil.api.traversor.HateoasLink.anyLink;
import static org.ignast.stockinvesting.testutil.api.traversor.HateoasLink.link;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

final class HateoasLinkTest {

    @Test
    public void shouldCreateLink() {
        final val link = link("company", "companyUri");

        Assertions.assertThat(link).isEqualTo("{\"_links\":{\"company\":{\"href\":\"companyUri\"}}}");
    }

    @Test
    public void shouldCreateAnyLink() {
        Assertions.assertThat(anyLink()).isEqualTo("{\"_links\":{\"any\":{\"href\":\"any\"}}}");
    }
}
