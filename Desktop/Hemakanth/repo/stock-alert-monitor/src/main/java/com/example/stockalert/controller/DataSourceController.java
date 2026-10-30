package com.example.stockalert.controller;

import com.example.stockalert.config.DataSourceProperties;
import com.example.stockalert.dto.DataSourceSettings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DataSourceController {

    private final DataSourceProperties dataSourceProperties;

    public DataSourceController(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @GetMapping("/datasource")
    public ResponseEntity<DataSourceSettings> getDataSource() {
        return ResponseEntity.ok(new DataSourceSettings(
                dataSourceProperties.getProvider(),
                dataSourceProperties.getApiKey(),
                dataSourceProperties.getBaseUrl()
        ));
    }

    @PostMapping("/datasource")
    public ResponseEntity<DataSourceSettings> updateDataSource(@RequestBody DataSourceSettings settings) {
        dataSourceProperties.setProvider(settings.provider());
        dataSourceProperties.setApiKey(settings.apiKey());
        dataSourceProperties.setBaseUrl(settings.baseUrl());
        return ResponseEntity.ok(new DataSourceSettings(
                dataSourceProperties.getProvider(),
                dataSourceProperties.getApiKey(),
                dataSourceProperties.getBaseUrl()
        ));
    }
}
