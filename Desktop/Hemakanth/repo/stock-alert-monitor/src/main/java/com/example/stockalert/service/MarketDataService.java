package com.example.stockalert.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface MarketDataService {

    BigDecimal fetchPrice(String symbol);

    Map<String, BigDecimal> fetchPrices(List<String> symbols);
}
