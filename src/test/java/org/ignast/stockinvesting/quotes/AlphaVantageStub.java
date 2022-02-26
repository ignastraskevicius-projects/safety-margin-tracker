package org.ignast.stockinvesting.quotes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.Builder;
import lombok.val;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.fluentjsonassert.JsonAssert.lenientAssertThatJson;
import static org.ignast.stockinvesting.quotes.AlphaVantageStub.returningPrice;
import static org.ignast.stockinvesting.quotes.QueryParams.validParamsBuilder;

public class AlphaVantageStub {
    public static void returningPrice() {
        stubFor(any(urlPathEqualTo("/query"))
                .willReturn(ok().withBody("{\"Error Message\":\"Some human-readable error message\"}")));
        stubFor(any(urlPathEqualTo("/query")).withQueryParam("function", equalTo("GLOBAL_QUOTE"))
                .withQueryParam("symbol", matching(".+")).withQueryParam("apikey", matching(".+"))
                .willReturn(ok().withBody("{\"Global Quote\":{\"05. price\":\"128.5000\"}}")));
    }
}

@WireMockTest
class AlphaVantageStubTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldReturnPrice(WireMockRuntimeInfo wireMock)
            throws IOException, InterruptedException, JSONException {
        returningPrice();

        int port = wireMock.getHttpPort();
        val response = query(port, "/query?" + validParamsBuilder().build().toString());

        assertThat(response.statusCode()).isEqualTo(200);
        lenientAssertThatJson(response.body()).isEqualTo("{\"Global Quote\":{\"05. price\":\"128.5000\"}}");
    }

    @Test
    public void queryingWithoutApiKeyShouldReturnError(WireMockRuntimeInfo wireMock)
            throws IOException, InterruptedException {
        returningPrice();
        int port = wireMock.getHttpPort();

        expectError(port, validParamsBuilder().apikey(null).build());
    }

    @Test
    public void queryingWithEmptyApiKeyShouldReturnError(WireMockRuntimeInfo wireMock)
            throws IOException, InterruptedException {
        returningPrice();
        int port = wireMock.getHttpPort();

        expectError(port, validParamsBuilder().apikey("").build());
    }

    @Test
    public void queryingWithoutFunctionParameterShouldReturnError(WireMockRuntimeInfo wireMock)
            throws IOException, InterruptedException {
        returningPrice();
        int port = wireMock.getHttpPort();

        expectError(port, validParamsBuilder().function(null).build());
    }

    @Test
    public void queryingNonGlobalQuoteFunctionShouldReturnError(WireMockRuntimeInfo wireMock)
            throws IOException, InterruptedException {
        returningPrice();
        int port = wireMock.getHttpPort();

        expectError(port, validParamsBuilder().function("unknownFunction").build());
    }

    @Test
    public void queryingWithoutSymbolShouldReturnError(WireMockRuntimeInfo wireMock)
            throws IOException, InterruptedException {
        returningPrice();
        int port = wireMock.getHttpPort();

        expectError(port, validParamsBuilder().symbol(null).build());
    }

    @Test
    public void queryingWithEmptySymbolShouldReturnError(WireMockRuntimeInfo wireMock)
            throws IOException, InterruptedException {
        returningPrice();
        int port = wireMock.getHttpPort();

        expectError(port, validParamsBuilder().symbol("").build());
    }

    private void expectError(int port, QueryParams uriQuery) throws IOException, InterruptedException {
        val response = query(port, String.format("/query?%s", uriQuery.toString()));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(jsonAsMap(response).keySet()).contains("Error Message");
    }

    private HashMap jsonAsMap(HttpResponse<String> response) throws JsonProcessingException {
        return mapper.readValue(response.body(), HashMap.class);
    }

    private HttpResponse<String> query(int port, String path) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:%d%s", port, path))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }
}

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "function", "symbol", "apikey" })
class QueryParams {
    String function;
    String apikey;
    String symbol;

    private static String API_KEY = "1OFDQOSYMBH3NP";

    private static String GLOBAL_QUOTE = "GLOBAL_QUOTE";

    public static QueryParamsBuilder validParamsBuilder() {
        return new QueryParamsBuilder().apikey(API_KEY).symbol("AMZN").function(GLOBAL_QUOTE);
    }

    @Override
    public String toString() {
        return new ObjectMapper().convertValue(this, new TypeReference<Map<String, String>>() {
        }).entrySet().stream().map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                .collect(Collectors.joining("&"));
    }
}

class QueryParamsBuilderTest {
    @Test
    public void shouldBuildWithFunction() {
        val queryParams = new QueryParams.QueryParamsBuilder().function("someFunction").build().toString();
        assertThat(queryParams).isEqualTo("function=someFunction");
    }

    @Test
    public void shouldBuildWithApikey() {
        val queryParams = new QueryParams.QueryParamsBuilder().apikey("someApikey").build().toString();
        assertThat(queryParams).isEqualTo("apikey=someApikey");
    }

    @Test
    public void shouldBuildWithSymbol() {
        val queryParams = new QueryParams.QueryParamsBuilder().symbol("someSymbol").build().toString();
        assertThat(queryParams).isEqualTo("symbol=someSymbol");
    }

    @Test
    public void shouldBuildWithNoParameters() {
        val queryParams = new QueryParams.QueryParamsBuilder().build().toString();
        assertThat(queryParams).isEqualTo("");
    }

    @Test
    public void shouldBuildWithMultiplParameters() {
        val queryParams = new QueryParams.QueryParamsBuilder().function("function1").symbol("symbol1").apikey("apikey1")
                .build().toString();
        assertThat(queryParams).isEqualTo("function=function1&symbol=symbol1&apikey=apikey1");
    }

    @Test
    public void shouldProvideBuilderWithValidParameters() {
        val queryParams = validParamsBuilder().build().toString();
        assertThat(queryParams).isEqualTo("function=GLOBAL_QUOTE&symbol=AMZN&apikey=1OFDQOSYMBH3NP");
    }
}