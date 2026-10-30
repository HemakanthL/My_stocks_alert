package com.example.stockalert.service;

import com.example.stockalert.config.DataSourceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Primary
public class ConfiguredMarketDataService implements MarketDataService {

    private final ApplicationContext applicationContext;
    private final DataSourceProperties dataSourceProperties;

    @Autowired
    public ConfiguredMarketDataService(ApplicationContext applicationContext, DataSourceProperties dataSourceProperties) {
        this.applicationContext = applicationContext;
        this.dataSourceProperties = dataSourceProperties;
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
        String provider = dataSourceProperties.getProvider() == null ? "mock" : dataSourceProperties.getProvider().trim().toLowerCase();

        return switch (provider) {
            case "alphavantage" -> applicationContext.getBean(AlphaVantageMarketDataService.class);
            case "yahoo" -> applicationContext.getBean(YahooFinanceMarketDataService.class);
            case "mock" -> applicationContext.getBean(MockMarketDataService.class);
            default -> applicationContext.getBean(MockMarketDataService.class);
        };
    }
}
