package org.ignast.stockinvesting.quotes.acceptance;

import lombok.val;
import org.ignast.stockinvesting.testutil.api.traversor.QuotesTraversor;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.quotes.acceptance.Uris.rootResourceOn;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompanyResourceTest {
    @Container
    private static final GenericContainer alphavantange = new GenericContainer(DockerImageName.parse("estimates/alphavantage-simulator:1.0-SNAPSHOT")).withExposedPorts(8080);

    @Container
    private static final MySQLContainer mysql = new MySQLContainer(DockerImageName.parse("org.ignast.stock-investing.quotes/mysql-dev:1.0-SNAPSHOT").asCompatibleSubstituteFor("mysql")).withPassword("test");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private QuotesTraversor.Factory quotesTraversors;

    @DynamicPropertySource
    private static void registedDatasource(DynamicPropertyRegistry registry) {
        registry.add("alphavantage.url", () -> format("http://localhost:%d", alphavantange.getMappedPort(8080)));
        registry.add("spring.datasource.url", () -> mysql.getJdbcUrl().replace("/test", "/quotes"));
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "test");
    }

    @Test
    public void shouldCreateCompany() throws JSONException {
        val company = quotesTraversors.startAt(rootResourceOn(port))
                .hop(f -> f.put("quotes:company", "{\"id\":5,\"name\":\"Microsoft\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"MSFT\"}]}"))
                .perform();
        assertThat(company.getStatusCode()).isEqualTo(CREATED);
        assertEquals("should create company", "{\"id\":5,\"name\":\"Microsoft\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"MSFT\"}]}", company.getBody(), false);
    }

    @Test
    public void shouldRetrieveCreatedCompany() throws JSONException {
        val company = quotesTraversors.startAt(rootResourceOn(port))
                .hop(f -> f.put("quotes:company", "{\"id\":4,\"name\":\"Microsoft\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}"))
                .hop(f -> f.get("self"))
                .perform();
        assertThat(company.getStatusCode()).isEqualTo(OK);
        assertEquals("should create company", "{\"id\":4,\"name\":\"Microsoft\",\"listings\":[{\"marketIdentifier\":\"XNAS\",\"stockSymbol\":\"AMZN\"}]}", company.getBody(), false);
    }
}
