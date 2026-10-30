package com.example.stockalert.config;

import com.example.stockalert.entity.AlertDirection;
import com.example.stockalert.entity.StockAlert;
import com.example.stockalert.repository.StockAlertRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class SeedDataConfig {

    @Bean
    public ApplicationRunner seedAlerts(StockAlertRepository stockAlertRepository) {
        return args -> {
            if (stockAlertRepository.count() == 0) {
                stockAlertRepository.saveAll(List.of(
                        new StockAlert("AAPL", AlertDirection.ABOVE, new BigDecimal("230.00"), true),
                        new StockAlert("MSFT", AlertDirection.ABOVE, new BigDecimal("420.00"), true),
                        new StockAlert("NVDA", AlertDirection.BELOW, new BigDecimal("100.00"), true)
                ));
            }
        };
    }
}
