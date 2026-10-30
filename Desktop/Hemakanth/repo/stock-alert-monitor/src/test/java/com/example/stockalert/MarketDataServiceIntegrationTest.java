package com.example.stockalert;

import com.example.stockalert.service.AlphaVantageMarketDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "stock.data.provider=alphavantage",
        "stock.data.api-key=test-api-key"
})
class MarketDataServiceIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldCreateAlphaVantageDataServiceWhenConfigured() {
        assertThat(applicationContext.getBean(AlphaVantageMarketDataService.class)).isNotNull();
    }
}
