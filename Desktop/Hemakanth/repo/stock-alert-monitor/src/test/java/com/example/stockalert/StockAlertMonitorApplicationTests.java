package com.example.stockalert;

import com.example.stockalert.service.StockAlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockAlertMonitorApplicationTests {

    @Autowired
    private StockAlertService stockAlertService;

    @Test
    void contextLoads() {
        assertThat(stockAlertService.getAllAlerts()).isNotEmpty();
        assertThat(stockAlertService.getTriggeredAlerts()).isNotNull();
    }
}
