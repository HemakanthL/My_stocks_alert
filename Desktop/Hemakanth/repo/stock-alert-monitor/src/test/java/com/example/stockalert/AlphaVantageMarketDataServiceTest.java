package com.example.stockalert;

import com.example.stockalert.config.DataSourceProperties;
import com.example.stockalert.service.AlphaVantageMarketDataService;
import com.example.stockalert.service.AlphaVantageMarketDataService.AlphaVantageQuoteResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlphaVantageMarketDataServiceTest {

    @Test
    void reportsMissingApiKeyInsteadOfReturningMockPrice() {
        DataSourceProperties properties = new DataSourceProperties();
        AlphaVantageMarketDataService service = new AlphaVantageMarketDataService(WebClient.builder(), properties);

        assertThatThrownBy(() -> service.fetchPrice("AAPL"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Configure the Alpha Vantage API key");
    }

    @Test
    void exposesProviderQuotaMessageWhenQuoteIsMissing() {
        AlphaVantageQuoteResponse response = new AlphaVantageQuoteResponse(
                null,
                "API call frequency is exceeded.",
                null,
                null
        );

        assertThat(response.providerMessage()).isEqualTo("API call frequency is exceeded.");
    }
}
