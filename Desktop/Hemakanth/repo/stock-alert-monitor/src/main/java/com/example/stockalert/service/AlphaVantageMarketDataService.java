package com.example.stockalert.service;

import com.example.stockalert.config.MarketApiProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@ConditionalOnExpression("('${stock.data.provider:mock}' == 'alphavantage') or ('${market.api.enabled:false}' == 'true')")
public class AlphaVantageMarketDataService implements MarketDataService {

    private final WebClient webClient;
    private final MarketApiProperties properties;

    public AlphaVantageMarketDataService(WebClient.Builder webClientBuilder, MarketApiProperties properties) {
        this.webClient = webClientBuilder.baseUrl(properties.getBaseUrl()).build();
        this.properties = properties;
    }

    @Override
    public BigDecimal fetchPrice(String symbol) {
        String normalizedSymbol = symbol.trim().toUpperCase(Locale.ROOT);

        if (properties.getKey() == null || properties.getKey().isBlank()) {
            return fallbackPrice(normalizedSymbol);
        }

        try {
            AlphaVantageQuoteResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/query")
                            .queryParam("function", "GLOBAL_QUOTE")
                            .queryParam("symbol", normalizedSymbol)
                            .queryParam("apikey", properties.getKey())
                            .build())
                    .retrieve()
                    .bodyToMono(AlphaVantageQuoteResponse.class)
                    .block();

            if (response == null || response.globalQuote() == null || response.globalQuote().price() == null) {
                return fallbackPrice(normalizedSymbol);
            }

            return new BigDecimal(response.globalQuote().price());
        } catch (WebClientResponseException exception) {
            return fallbackPrice(normalizedSymbol);
        } catch (RuntimeException exception) {
            return fallbackPrice(normalizedSymbol);
        }
    }

    @Override
    public Map<String, BigDecimal> fetchPrices(List<String> symbols) {
        return symbols.stream()
                .map(String::trim)
                .filter(symbol -> !symbol.isBlank())
                .collect(java.util.stream.Collectors.toMap(
                        symbol -> symbol.toUpperCase(Locale.ROOT),
                        this::fetchPrice,
                        (left, right) -> right
                ));
    }

    private BigDecimal fallbackPrice(String symbol) {
        return Map.of(
                "AAPL", new BigDecimal("232.48"),
                "MSFT", new BigDecimal("425.20"),
                "NVDA", new BigDecimal("97.10"),
                "AMZN", new BigDecimal("186.70"),
                "GOOGL", new BigDecimal("176.30"),
                "TSLA", new BigDecimal("241.40")
        ).getOrDefault(symbol, new BigDecimal("100.00"));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AlphaVantageQuoteResponse(@JsonProperty("Global Quote") GlobalQuote globalQuote) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GlobalQuote(@JsonProperty("05. price") String price) {
    }
}
