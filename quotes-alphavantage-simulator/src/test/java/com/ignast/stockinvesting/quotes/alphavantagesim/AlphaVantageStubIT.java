package com.ignast.stockinvesting.quotes.alphavantagesim;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.ignast.stockinvesting.quotes.alphavantagesim.QueryParams.validParamsBuilder;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.testutil.api.JsonAssert.assertThatJson;

public final class AlphaVantageStubIT {

    @RegisterExtension
    private static final WireMockExtension WIREMOCK = WireMockExtension.newInstance().options(wireMockConfig().usingFilesUnderDirectory("src/main/resources/wiremock/")).build();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldReturnPrice() throws IOException, InterruptedException, JSONException {
        final val response = query("/query?" + validParamsBuilder().build().toString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThatJson(response.body()).isEqualTo("{\"Global Quote\":{\"05. price\":\"128.5000\"}}");
        assertThat(response.headers().allValues("Content-Type")).contains("application/json");
    }

    @Test
    public void shouldReturnNoPriceForAAAA() throws IOException, InterruptedException, JSONException {
        final val response = query("/query?" + validParamsBuilder().symbol("AAAA").build().toString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThatJson(response.body()).isEqualTo("{\"Global Quote\":{}}");
        assertThat(response.headers().allValues("Content-Type")).contains("application/json");
    }

    @Test
    public void queryingWithoutApiKeyShouldReturnError() throws IOException, InterruptedException {
        expectError(validParamsBuilder().apikey(null).build());
    }

    @Test
    public void queryingWithEmptyApiKeyShouldReturnError() throws IOException, InterruptedException {
        expectError(validParamsBuilder().apikey("").build());
    }

    @Test
    public void queryingWithoutFunctionParameterShouldReturnError() throws IOException, InterruptedException {
        expectError(validParamsBuilder().function(null).build());
    }

    @Test
    public void queryingNonGlobalQuoteFunctionShouldReturnError() throws IOException, InterruptedException {
        expectError(validParamsBuilder().function("unknownFunction").build());
    }

    @Test
    public void queryingWithoutSymbolShouldReturnError() throws IOException, InterruptedException {
        expectError(validParamsBuilder().symbol(null).build());
    }

    @Test
    public void queryingWithEmptySymbolShouldReturnError() throws IOException, InterruptedException {
        expectError(validParamsBuilder().symbol("").build());
    }

    private void expectError(final QueryParams uriQuery) throws IOException, InterruptedException {
        final val response = query(format("/query?%s", uriQuery.toString()));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(jsonAsMap(response).keySet()).contains("Error Message");
        assertThat(response.headers().allValues("Content-Type")).contains("application/json");
    }

    private HashMap jsonAsMap(final HttpResponse<String> response) throws JsonProcessingException {
        return mapper.readValue(response.body(), HashMap.class);
    }

    private HttpResponse<String> query(final String path) throws IOException, InterruptedException {
        final val client = HttpClient.newHttpClient();
        final val request = HttpRequest.newBuilder()
                .uri(URI.create(format("http://localhost:%d%s", WIREMOCK.getPort(), path))).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}

@Testcontainers
final class DockerizedAlphaVantageStubIT {
    private static final String DOCKER_IMAGE_NAME = System.getProperty("docker.image");

    @Container
    public static final GenericContainer CONTAINER = new GenericContainer(DockerImageName.parse(DOCKER_IMAGE_NAME)).withExposedPorts(8080);

    @Test
    public void shouldContainQuotedPrices() throws IOException, InterruptedException {
        final val client = HttpClient.newHttpClient();
        final val port = CONTAINER.getMappedPort(8080);
        final val uri = format("http://localhost:%d/query?%s", port, validParamsBuilder().build().toString());
        final val request = HttpRequest.newBuilder().uri(URI.create(uri)).build();

        final val response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.body()).startsWith("{\"Global Quote");
    }
}

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "function", "symbol", "apikey" })
final class QueryParams {
    private static String API_KEY = "1OFDQOSYMBH3NP";

    private static String GLOBAL_QUOTE = "GLOBAL_QUOTE";

    private final String function;

    private final String apikey;

    private final String symbol;

    public static QueryParamsBuilder validParamsBuilder() {
        return new QueryParamsBuilder().apikey(API_KEY).symbol("AMZN").function(GLOBAL_QUOTE);
    }

    @Override
    public String toString() {
        return new ObjectMapper().convertValue(this, new TypeReference<Map<String, String>>() {
        }).entrySet().stream().map(e -> format("%s=%s", e.getKey(), e.getValue()))
                .collect(Collectors.joining("&"));
    }
}

final class QueryParamsBuilderTest {
    @Test
    public void shouldBuildWithFunction() {
        final val queryParams = new QueryParams.QueryParamsBuilder().function("someFunction").build().toString();
        assertThat(queryParams).isEqualTo("function=someFunction");
    }

    @Test
    public void shouldBuildWithApikey() {
        final val queryParams = new QueryParams.QueryParamsBuilder().apikey("someApikey").build().toString();
        assertThat(queryParams).isEqualTo("apikey=someApikey");
    }

    @Test
    public void shouldBuildWithSymbol() {
        final val queryParams = new QueryParams.QueryParamsBuilder().symbol("someSymbol").build().toString();
        assertThat(queryParams).isEqualTo("symbol=someSymbol");
    }

    @Test
    public void shouldBuildWithNoParameters() {
        final val queryParams = new QueryParams.QueryParamsBuilder().build().toString();
        assertThat(queryParams).isEqualTo("");
    }

    @Test
    public void shouldBuildWithMultiplParameters() {
        final val queryParams = new QueryParams.QueryParamsBuilder().function("function1").symbol("symbol1").apikey("apikey1")
                .build().toString();
        assertThat(queryParams).isEqualTo("function=function1&symbol=symbol1&apikey=apikey1");
    }

    @Test
    public void shouldProvideBuilderWithValidParameters() {
        final val queryParams = validParamsBuilder().build().toString();
        assertThat(queryParams).isEqualTo("function=GLOBAL_QUOTE&symbol=AMZN&apikey=1OFDQOSYMBH3NP");
    }
}