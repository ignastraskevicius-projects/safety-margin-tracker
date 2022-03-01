package org.ignast.stockinvesting.quotes;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;
import org.ignast.stockinvesting.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.domain.StockQuotes;
import org.ignast.stockinvesting.domain.StockSymbol;
import org.ignast.stockinvesting.domain.StockSymbolNotSupported;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static java.lang.String.format;

@Repository
public class AlphaVantageQuotes implements StockQuotes {

    private String url;

    private String apikey;

    private RestTemplate restTemplate;

    public AlphaVantageQuotes(RestTemplateBuilder builder, @Value("${alphavantage.url}") String url,
            @Value("${alphavantage.apikey}") String apikey) {
        restTemplate = builder.messageConverters().build();
        this.url = url;
        this.apikey = apikey;
    }

    @Override
    public BigDecimal getQuotedPriceOf(StockSymbol stockSymbol, MarketIdentifierCode mic) {
        val response = invoke(toUri(stockSymbol)).getBody();

        return response.getQuote().map(q -> q.getPrice().orElseThrow(() -> stockSymbolNotSupported(stockSymbol, mic)))
                .orElseThrow(() -> {
                    throw quoteRetrievalFailed(response.getError());
                });

    }

    private QuoteRetrievalFailedException quoteRetrievalFailed(Optional<String> errorMessage) {
        if (errorMessage.isPresent()) {
            return new QuoteRetrievalFailedException("Message from remote server: " + errorMessage.get());
        } else {
            return new QuoteRetrievalFailedException("Communication with server failed");
        }
    }

    private String toUri(StockSymbol stockSymbol) {
        return url + format("/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", stockSymbol.get(), apikey);
    }

    private StockSymbolNotSupported stockSymbolNotSupported(StockSymbol stockSymbol, MarketIdentifierCode mic) {
        return new StockSymbolNotSupported(format("Stock symbol '%s' in market '%s' is not supported by this service",
                stockSymbol.get(), mic.get()));
    }

    private ResponseEntity<QuoteResponseDTO> invoke(String uri) {
        try {
            return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(HttpHeaders.EMPTY),
                    QuoteResponseDTO.class);
        } catch (Exception e) {
            throw new QuoteRetrievalFailedException("Communication with server failed", e);
        }
    }
}

@ToString
class QuoteResponseDTO {
    @Getter
    private Optional<QuoteDTO> quote;
    @Getter
    private Optional<String> error;

    public QuoteResponseDTO(@NonNull @JsonProperty("Global Quote") Optional<QuoteDTO> quote,
            @NonNull @JsonProperty("Error Message") Optional<String> error) {
        this.quote = quote;
        this.error = error;
    }
}

@ToString
class QuoteDTO {
    @Getter
    private Optional<BigDecimal> price;

    public QuoteDTO(@NonNull @JsonProperty(value = "05. price") Optional<BigDecimal> price) {
        this.price = price;
    }
}
