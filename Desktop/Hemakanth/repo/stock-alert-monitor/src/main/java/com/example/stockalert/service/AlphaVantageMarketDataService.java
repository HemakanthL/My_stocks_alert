package com.example.stockalert.service;

import com.example.stockalert.config.DataSourceProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AlphaVantageMarketDataService implements MarketDataService {

    private final WebClient.Builder webClientBuilder;
    private final DataSourceProperties properties;

    public AlphaVantageMarketDataService(WebClient.Builder webClientBuilder, DataSourceProperties properties) {
        this.webClientBuilder = webClientBuilder;
        this.properties = properties;
    }

    @Override
    public BigDecimal fetchPrice(String symbol) {
        String normalizedSymbol = symbol.trim().toUpperCase(Locale.ROOT);

        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new IllegalStateException("Configure the Alpha Vantage API key before requesting live prices.");
        }

        try {
            AlphaVantageQuoteResponse response = webClientBuilder.clone()
                    .baseUrl(normalizedBaseUrl())
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/query")
                            .queryParam("function", "GLOBAL_QUOTE")
                            .queryParam("symbol", normalizedSymbol)
                            .queryParam("apikey", properties.getApiKey())
                            .build())
                    .retrieve()
                    .bodyToMono(AlphaVantageQuoteResponse.class)
                    .block();

            if (response == null || response.globalQuote() == null || response.globalQuote().price() == null) {
                String providerMessage = response == null ? null : response.providerMessage();
                String detail = providerMessage == null || providerMessage.isBlank()
                        ? "Check the symbol, API key, and provider usage limits."
                        : redactApiKey(providerMessage);
                throw new IllegalStateException(
                        "Alpha Vantage returned no quote for " + normalizedSymbol + ". " + detail
                );
            }

            return new BigDecimal(response.globalQuote().price());
        } catch (WebClientResponseException exception) {
            throw new IllegalStateException(
                    "Alpha Vantage request failed with HTTP " + exception.getStatusCode().value() + ".",
                    exception
            );
        } catch (WebClientRequestException exception) {
            throw new IllegalStateException("Could not connect to Alpha Vantage. Check the network and base URL.", exception);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Alpha Vantage returned an invalid price for " + normalizedSymbol + ".", exception);
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

    private String normalizedBaseUrl() {
        String baseUrl = properties.getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            return "https://www.alphavantage.co";
        }
        return baseUrl.trim()
                .replaceFirst("(?i)://(www\\.)?alphavantage\\.com(?=[:/]|$)", "://www.alphavantage.co")
                .replaceAll("/+$", "");
    }

    private String redactApiKey(String message) {
        String apiKey = properties.getApiKey();
        return apiKey == null || apiKey.isBlank() ? message : message.replace(apiKey, "[redacted]");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AlphaVantageQuoteResponse(
            @JsonProperty("Global Quote") GlobalQuote globalQuote,
            @JsonProperty("Note") String note,
            @JsonProperty("Information") String information,
            @JsonProperty("Error Message") String errorMessage
    ) {
        public String providerMessage() {
            if (note != null && !note.isBlank()) {
                return note;
            }
            if (information != null && !information.isBlank()) {
                return information;
            }
            return errorMessage;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GlobalQuote(@JsonProperty("05. price") String price) {
    }
}
