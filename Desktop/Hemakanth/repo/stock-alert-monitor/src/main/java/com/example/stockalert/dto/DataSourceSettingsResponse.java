package com.example.stockalert.dto;

import com.example.stockalert.config.DataSourceProperties;

public record DataSourceSettingsResponse(String provider, boolean apiKeyConfigured, String baseUrl) {

    public static DataSourceSettingsResponse from(DataSourceProperties properties) {
        String apiKey = properties.getApiKey();
        return new DataSourceSettingsResponse(
                properties.getProvider(),
                apiKey != null && !apiKey.isBlank(),
                properties.getBaseUrl()
        );
    }
}
