package com.example.stockalert.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@ConditionalOnExpression("('${stock.data.provider:mock}' == 'mock') and ('${market.api.enabled:false}' != 'true')")
public class MockMarketDataService implements MarketDataService {

    private static final Map<String, BigDecimal> BASE_PRICES = Map.of(
            "AAPL", new BigDecimal("232.48"),
            "MSFT", new BigDecimal("425.20"),
            "NVDA", new BigDecimal("97.10"),
            "AMZN", new BigDecimal("186.70"),
            "GOOGL", new BigDecimal("176.30"),
            "TSLA", new BigDecimal("241.40")
    );

    @Override
    public BigDecimal fetchPrice(String symbol) {
        return BASE_PRICES.getOrDefault(symbol.trim().toUpperCase(), new BigDecimal("100.00"));
    }

    @Override
    public Map<String, BigDecimal> fetchPrices(List<String> symbols) {
        return symbols.stream()
                .map(String::trim)
                .filter(symbol -> !symbol.isBlank())
                .collect(Collectors.toMap(
                        symbol -> symbol.toUpperCase(),
                        this::fetchPrice,
                        (left, right) -> right
                ));
    }
}
