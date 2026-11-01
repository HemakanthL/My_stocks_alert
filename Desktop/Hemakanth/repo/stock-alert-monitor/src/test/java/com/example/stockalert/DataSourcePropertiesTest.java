package com.example.stockalert;

import com.example.stockalert.config.DataSourceProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataSourcePropertiesTest {

    @Test
    void normalizesProviderClassNameToDashboardProviderValue() {
        DataSourceProperties properties = new DataSourceProperties();

        properties.setProvider("AlphaVantageMarketDataService");

        assertThat(properties.getProvider()).isEqualTo("alphavantage");
    }

    @Test
    void normalizesProviderCaseAndWhitespace() {
        DataSourceProperties properties = new DataSourceProperties();

        properties.setProvider("  ALPHAVANTAGE  ");

        assertThat(properties.getProvider()).isEqualTo("alphavantage");
    }
}
