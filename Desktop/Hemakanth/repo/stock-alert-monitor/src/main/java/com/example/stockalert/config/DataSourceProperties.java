package com.example.stockalert.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

@ConfigurationProperties(prefix = "stock.data")
public class DataSourceProperties {

    private String provider = "mock";
    private String apiKey = "";
    private String baseUrl = "https://www.alphavantage.co";

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        if (provider == null || provider.isBlank()) {
            this.provider = "mock";
            return;
        }

        this.provider = switch (provider.trim().toLowerCase(Locale.ROOT)) {
            case "alphavantage", "alphavantagemarketdataservice" -> "alphavantage";
            case "yahoo", "yahoofinancemarketdataservice" -> "yahoo";
            case "mock", "mockmarketdataservice" -> "mock";
            default -> provider.trim().toLowerCase(Locale.ROOT);
        };
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
