package org.ignast.stockinvesting.api.acceptance.alphavantage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.Builder;
import lombok.val;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

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
import static org.ignast.stockinvesting.api.fluentjsonassert.JsonAssert.assertThatJson;
import static org.ignast.stockinvesting.api.acceptance.alphavantage.QueryParams.validParamsBuilder;

public class AlphaVantageStub {

    private WireMockExtension wireMock;

    public AlphaVantageStub(WireMockExtension wireMock) {

        this.wireMock = wireMock;
    }

    public void stubPriceForAllSymbolsButAAAA() {
        wireMock.stubFor(any(urlPathEqualTo("/query"))
                .willReturn(ok().withBody("{\"Error Message\":\"Some human-readable error message\"}")));
        wireMock.stubFor(any(urlPathEqualTo("/query")).withQueryParam("function", equalTo("GLOBAL_QUOTE"))
                .withQueryParam("symbol", matching(".+")).withQueryParam("apikey", matching(".+"))
                .willReturn(ok().withBody("{\"Global Quote\":{\"05. price\":\"128.5000\"}}")));
        wireMock.stubFor(any(urlPathEqualTo("/query")).withQueryParam("function", equalTo("GLOBAL_QUOTE"))
                .withQueryParam("symbol", matching("AAAA")).withQueryParam("apikey", matching(".+"))
                .willReturn(ok().withBody("{\"Global Quote\":{}}")));
    }
}

class AlphaVantageStubTest {

    private ObjectMapper mapper = new ObjectMapper();

    @RegisterExtension
    private WireMockExtension wireMock = WireMockExtension.newInstance().build();

    private AlphaVantageStub alphaVantageStub;

    @BeforeEach
    public void setup() {
        alphaVantageStub = new AlphaVantageStub(wireMock);
    }

    @Test
    public void shouldReturnPrice() throws IOException, InterruptedException, JSONException {
        alphaVantageStub.stubPriceForAllSymbolsButAAAA();

        val response = query("/query?" + validParamsBuilder().build().toString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThatJson(response.body()).isEqualTo("{\"Global Quote\":{\"05. price\":\"128.5000\"}}");
    }

    @Test
    public void shouldReturnNoPriceForAAAA() throws IOException, InterruptedException, JSONException {
        alphaVantageStub.stubPriceForAllSymbolsButAAAA();

        val response = query("/query?" + validParamsBuilder().symbol("AAAA").build().toString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThatJson(response.body()).isEqualTo("{\"Global Quote\":{}}");
    }

    @Test
    public void queryingWithoutApiKeyShouldReturnError() throws IOException, InterruptedException {
        alphaVantageStub.stubPriceForAllSymbolsButAAAA();

        expectError(validParamsBuilder().apikey(null).build());
    }

    @Test
    public void queryingWithEmptyApiKeyShouldReturnError() throws IOException, InterruptedException {
        alphaVantageStub.stubPriceForAllSymbolsButAAAA();

        expectError(validParamsBuilder().apikey("").build());
    }

    @Test
    public void queryingWithoutFunctionParameterShouldReturnError() throws IOException, InterruptedException {
        alphaVantageStub.stubPriceForAllSymbolsButAAAA();

        expectError(validParamsBuilder().function(null).build());
    }

    @Test
    public void queryingNonGlobalQuoteFunctionShouldReturnError() throws IOException, InterruptedException {
        alphaVantageStub.stubPriceForAllSymbolsButAAAA();

        expectError(validParamsBuilder().function("unknownFunction").build());
    }

    @Test
    public void queryingWithoutSymbolShouldReturnError() throws IOException, InterruptedException {
        alphaVantageStub.stubPriceForAllSymbolsButAAAA();

        expectError(validParamsBuilder().symbol(null).build());
    }

    @Test
    public void queryingWithEmptySymbolShouldReturnError() throws IOException, InterruptedException {
        alphaVantageStub.stubPriceForAllSymbolsButAAAA();

        expectError(validParamsBuilder().symbol("").build());
    }

    private void expectError(QueryParams uriQuery) throws IOException, InterruptedException {
        val response = query(String.format("/query?%s", uriQuery.toString()));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(jsonAsMap(response).keySet()).contains("Error Message");
    }

    private HashMap jsonAsMap(HttpResponse<String> response) throws JsonProcessingException {
        return mapper.readValue(response.body(), HashMap.class);
    }

    private HttpResponse<String> query(String path) throws IOException, InterruptedException {
        val client = HttpClient.newHttpClient();
        val request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:%d%s", wireMock.getPort(), path))).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
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