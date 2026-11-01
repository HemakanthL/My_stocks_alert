package com.example.stockalert.service;

import com.example.stockalert.config.DataSourceProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Primary
public class ConfiguredMarketDataService implements MarketDataService {

    private final DataSourceProperties dataSourceProperties;
    private final AlphaVantageMarketDataService alphaVantageMarketDataService;
    private final YahooFinanceMarketDataService yahooFinanceMarketDataService;
    private final MockMarketDataService mockMarketDataService;

    public ConfiguredMarketDataService(
            DataSourceProperties dataSourceProperties,
            AlphaVantageMarketDataService alphaVantageMarketDataService,
            YahooFinanceMarketDataService yahooFinanceMarketDataService,
            MockMarketDataService mockMarketDataService
    ) {
        this.dataSourceProperties = dataSourceProperties;
        this.alphaVantageMarketDataService = alphaVantageMarketDataService;
        this.yahooFinanceMarketDataService = yahooFinanceMarketDataService;
        this.mockMarketDataService = mockMarketDataService;
    }

    @Override
    public BigDecimal fetchPrice(String symbol) {
        return resolveDelegate().fetchPrice(symbol);
    }

    @Override
    public Map<String, BigDecimal> fetchPrices(List<String> symbols) {
        return resolveDelegate().fetchPrices(symbols);
    }

    private MarketDataService resolveDelegate() {
        String provider = dataSourceProperties.getProvider() == null
                ? "mock"
                : dataSourceProperties.getProvider().trim().toLowerCase(Locale.ROOT);

        return switch (provider) {
            case "alphavantage" -> alphaVantageMarketDataService;
            case "yahoo" -> yahooFinanceMarketDataService;
            case "mock" -> mockMarketDataService;
            default -> throw new IllegalStateException("Unsupported stock data provider: " + provider);
        };
    }
}
