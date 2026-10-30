package com.example.stockalert;

import com.example.stockalert.config.DataSourceProperties;
import com.example.stockalert.config.MarketApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = {MarketApiProperties.class, DataSourceProperties.class})
@EnableScheduling
public class StockAlertMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockAlertMonitorApplication.class, args);
    }
}
