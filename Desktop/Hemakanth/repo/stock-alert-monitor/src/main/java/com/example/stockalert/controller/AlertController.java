package com.example.stockalert.controller;

import com.example.stockalert.dto.AlertRequest;
import com.example.stockalert.dto.AlertResponse;
import com.example.stockalert.entity.StockAlert;
import com.example.stockalert.service.StockAlertService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AlertController {

    private final StockAlertService stockAlertService;

    public AlertController(StockAlertService stockAlertService) {
        this.stockAlertService = stockAlertService;
    }

    @GetMapping("/alerts")
    public List<AlertResponse> getAlerts() {
        return stockAlertService.getAllAlerts().stream()
                .map(AlertResponse::fromEntity)
                .toList();
    }

    @GetMapping("/alerts/triggered")
    public List<AlertResponse> getTriggeredAlerts() {
        return stockAlertService.getTriggeredAlerts().stream()
                .map(AlertResponse::fromEntity)
                .toList();
    }

    @GetMapping("/alerts/{id}")
    public ResponseEntity<AlertResponse> getAlertById(@PathVariable Long id) {
        return stockAlertService.getAlertById(id)
                .map(stockAlert -> ResponseEntity.ok(AlertResponse.fromEntity(stockAlert)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/alerts")
    public ResponseEntity<AlertResponse> createAlert(@Valid @RequestBody AlertRequest request) {
        StockAlert stockAlert = stockAlertService.createAlert(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(AlertResponse.fromEntity(stockAlert));
    }

    @PutMapping("/alerts/{id}")
    public ResponseEntity<AlertResponse> updateAlert(@PathVariable Long id, @Valid @RequestBody AlertRequest request) {
        try {
            StockAlert stockAlert = stockAlertService.updateAlert(id, request);
            return ResponseEntity.ok(AlertResponse.fromEntity(stockAlert));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/alerts/{id}/toggle")
    public ResponseEntity<AlertResponse> toggleAlert(@PathVariable Long id) {
        try {
            StockAlert stockAlert = stockAlertService.toggleAlert(id);
            return ResponseEntity.ok(AlertResponse.fromEntity(stockAlert));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/alerts/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        try {
            stockAlertService.deleteAlert(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }
}
