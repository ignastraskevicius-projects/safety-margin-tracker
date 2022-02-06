package org.ignast.stockinvesting.api.test;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.json.JSONObject;

public class HateoasJsonMatchers {
    public static HasRel hasRel(String relName) {
        return new HasRel(relName);
    }

    public static class HasRel {
        private final String relName;

        public HasRel(String relName) {
            this.relName = relName;
        }

        public TypeSafeMatcher<String> withHrefContaining(String hrefSubstring) {
            return new ExistsRelWithHrefContainingString(relName, hrefSubstring);
        }

        static class ExistsRelWithHrefContainingString extends TypeSafeMatcher<String> {
            private String relName;
            private String hrefSubstring;

            public ExistsRelWithHrefContainingString(String relName, String hrefSubstring) {
                this.relName = relName;
                this.hrefSubstring = hrefSubstring;
            }

            @Override
            protected boolean matchesSafely(String hateoasJson) {
                try {
                    return new JSONObject(hateoasJson).getJSONObject("_links").getJSONObject(relName).getString("href")
                            .contains(hrefSubstring);
                } catch (JSONException e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(
                        String.format("HATEOAS json should contain a '%s' rel with a href containing substring '%s'",
                                relName, hrefSubstring));
            }
        }
    }
}
