package org.ignast.stockinvesting.testutil.api.traversor;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

public class HopTest {

    private RestTemplate restTemplate = mock(RestTemplate.class);

    private HrefExtractor hrefExtractor = mock(HrefExtractor.class);

    private Hop.Factory hopFactory = new Hop.Factory(APP_V1, restTemplate, hrefExtractor);

    private ArgumentCaptor<HttpEntity<String>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

    private static final MediaType APP_V1 = MediaType.parseMediaType("application/app.specific.media.type-v1.hal+json");

    @Test
    public void factoryShouldNotBeCreatedWithNullAppMediaType() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Hop.Factory(null, mock(RestTemplate.class), mock(HrefExtractor.class)));
    }

    @Test
    public void factoryShouldNotBeCreatedWithNullRestTemplate() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Hop.Factory(APP_V1, null, mock(HrefExtractor.class)));
    }

    @Test
    public void factoryShouldNotBeCreatedWithNullHrefExtractor() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Hop.Factory(APP_V1, mock(RestTemplate.class), null));
    }

    @Test
    public void factoryShouldBeCreatedWithNonNullArguments() {
        new Hop.Factory(mock(MediaType.class), mock(RestTemplate.class), mock(HrefExtractor.class));
    }

    @Test
    public void hopsTraversalShouldNotAcceptNullEntities() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> hopFactory.get("any").traverse(null));
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> hopFactory.put("any", "request").traverse(null));
    }

    @Test
    public void getHopShouldNotAcceptNullRels() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> hopFactory.get(null));
    }

    @Test
    public void shouldTraverseGetHop() {
        when(restTemplate.exchange(any(String.class), any(), any(), any(Class.class))).thenReturn(status(OK).body("hopResponse"));
        val responseWithLinkToCompany = status(OK).contentType(APP_V1).body(HateoasLink.link("company", "companyUri"));
        when(hrefExtractor.extractHref(responseWithLinkToCompany, "company")).thenReturn("companyUri");

        val companyResponse = hopFactory.get("company").traverse(responseWithLinkToCompany);

        verify(restTemplate).exchange(eq("companyUri"), eq(GET), entityCaptor.capture(), eq(String.class));
        assertThat(entityCaptor.getValue().getHeaders().get("Accept")).isEqualTo(asList(APP_V1.toString()));
        assertThat(companyResponse.getBody()).isEqualTo("hopResponse");
    }

    @Test
    public void getHopTraversalShouldFailWhenHrefCannotBeExtracted() {
        val response = mock(ResponseEntity.class);
        when(hrefExtractor.extractHref(response, "any")).thenThrow(TestException.class);

        assertThatExceptionOfType(TestException.class)
                .isThrownBy(() -> hopFactory.get("any").traverse(response));
    }

    @Test
    public void putHopShouldNotAcceptNullRels() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> hopFactory.put(null, "request"));
    }

    @Test
    public void putHopShouldNotAcceptNullRequests() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> hopFactory.put("company", null));
    }

    @Test
    public void shouldTraversePutHop() {
        when(restTemplate.exchange(any(String.class), any(), any(), any(Class.class))).thenReturn(status(OK).body("hopResponse"));
        val responseWithLinkToCompany = status(OK).contentType(APP_V1).body("{\"_links\":{\"client\":{\"href\":\"clientUri\"}}}");
        when(hrefExtractor.extractHref(responseWithLinkToCompany, "client")).thenReturn("clientUri");

        val companyResponse = hopFactory.put("client", "hopRequest").traverse(responseWithLinkToCompany);

        verify(restTemplate).exchange(eq("clientUri"), eq(PUT), entityCaptor.capture(), eq(String.class));
        assertThat(entityCaptor.getValue().getHeaders().get("Content-Type")).isEqualTo(asList(APP_V1.toString()));
        assertThat(entityCaptor.getValue().getBody()).isEqualTo("hopRequest");
        assertThat(companyResponse.getBody()).isEqualTo("hopResponse");
    }

    @Test
    public void putHopTraversalShouldFailWhenHrefCannotBeExtracted() {
        val response = mock(ResponseEntity.class);
        when(hrefExtractor.extractHref(response, "any")).thenThrow(TestException.class);

        assertThatExceptionOfType(TestException.class)
                .isThrownBy(() -> hopFactory.put("any", "anyRequest").traverse(response));
    }

    private class TestException extends RuntimeException {

    }
}

