package org.ignast.stockinvesting.quotes.api.controller.integration.root;

import static java.lang.String.format;
import static org.ignast.stockinvesting.quotes.api.controller.integration.root.RootControllerIT.DOCS_URL;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.resourceLinksMatchesJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.ignast.stockinvesting.quotes.api.controller.HalConfig;
import org.ignast.stockinvesting.quotes.api.controller.errorhandler.AppErrorsHandlingConfiguration;
import org.ignast.stockinvesting.quotes.api.controller.root.RootController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@TestPropertySource("documentation.url=" + DOCS_URL)
@WebMvcTest({ RootController.class, HalConfig.class, AppErrorsHandlingConfiguration.class })
public class RootControllerIT {

    static final String DOCS_URL = "http://documentation:8081";

    private static final String APP_V1 = "application/vnd.stockinvesting.quotes-v1.hal+json";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldContainLinks() throws Exception {
        mockMvc
            .perform(get("/").accept(APP_V1))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", APP_V1))
            .andExpect(
                resourceLinksMatchesJson(
                    format(
                        """
                        {"_links":{
                            "quotes:createCompany":{"href":"http://localhost/companies"},
                            "curies":[{
                                "name":"quotes",
                                "href":"http://localhost:8081/rels/quotes/{rel}",
                                "templated":true
                            }]
                        }}""",
                        DOCS_URL
                    )
                )
            );
    }
}
