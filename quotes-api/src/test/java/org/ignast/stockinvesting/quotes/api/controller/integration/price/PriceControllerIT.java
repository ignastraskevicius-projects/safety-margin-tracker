package org.ignast.stockinvesting.quotes.api.controller.integration.price;

import static java.lang.String.format;
import static org.ignast.stockinvesting.quotes.api.testutil.DomainFactoryForTests.amazon;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.resourceContentMatchesJson;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.ignast.stockinvesting.quotes.api.controller.PriceController;
import org.ignast.stockinvesting.quotes.api.controller.errorhandler.AppErrorsHandlingConfiguration;
import org.ignast.stockinvesting.quotes.domain.Companies;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ PriceController.class, AppErrorsHandlingConfiguration.class })
public class PriceControllerIT {

    private static final String APP_V1 = "application/vnd.stockinvesting.quotes-v1.hal+json";

    @MockBean
    private Companies companies;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void shouldGetPrice() throws Exception {
        final val amazonId = amazon().getExternalId();
        final val uri = format("/companies/%d/price", amazonId.get());
        when(companies.findByExternalId(amazonId)).thenReturn(amazon());

        mockMvc
            .perform(get(uri).accept(APP_V1))
            .andExpect(status().isOk())
            .andExpect(
                resourceContentMatchesJson("""
                        {"amount":"3000","currency":"USD"}""")
            );
    }

    @Test
    public void shouldNotAcceptNonHalJson() throws Exception {
        final val uri = format("/companies/%d/price", amazon().getExternalId().get());

        mockMvc.perform(get(uri).accept("application/json")).andExpect(status().isNotAcceptable());
    }

    @Test
    public void shouldNotAcceptUnversionedHalJson() throws Exception {
        final val uri = format("/companies/%d/price", amazon().getExternalId().get());

        mockMvc.perform(get(uri).accept(MediaTypes.HAL_JSON)).andExpect(status().isNotAcceptable());
    }

    @Test
    public void shouldNotBeModifiable() throws Exception {
        final val uri = format("/companies/%d/price", amazon().getExternalId().get());

        mockMvc
            .perform(put(uri).accept(MediaTypes.HAL_JSON).content("any"))
            .andExpect(status().isMethodNotAllowed());
    }
}
