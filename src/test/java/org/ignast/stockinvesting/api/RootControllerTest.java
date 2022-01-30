package org.ignast.stockinvesting.api;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.ignast.stockinvesting.api.test.HateoasJsonMatchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.test.HateoasJsonMatchers.hasRel;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RootController.class)
public class RootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void rootShouldContainLinkToCompanies() throws Exception {
        try {
            ResultActions mvcResult = mockMvc.perform(get("/"));
            ResultActions resultExpectation = mvcResult.andExpect(status().isOk());
            resultExpectation.andExpect(content().string(hasRel("stocks:company").withHrefContaining("/companies")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

