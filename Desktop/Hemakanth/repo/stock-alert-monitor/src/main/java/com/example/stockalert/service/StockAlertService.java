package com.example.stockalert.service;

import com.example.stockalert.dto.AlertRequest;
import com.example.stockalert.entity.AlertDirection;
import com.example.stockalert.entity.StockAlert;
import com.example.stockalert.repository.StockAlertRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class StockAlertService {

    private final StockAlertRepository stockAlertRepository;
    private final MarketDataService marketDataService;

    public StockAlertService(StockAlertRepository stockAlertRepository, MarketDataService marketDataService) {
        this.stockAlertRepository = stockAlertRepository;
        this.marketDataService = marketDataService;
    }

    public List<StockAlert> getAllAlerts() {
        return stockAlertRepository.findAll();
    }

    public List<StockAlert> getTriggeredAlerts() {
        return stockAlertRepository.findAll().stream()
                .filter(stockAlert -> stockAlert.getLastTriggeredMessage() != null && !stockAlert.getLastTriggeredMessage().isBlank())
                .toList();
    }

    public Optional<StockAlert> getAlertById(Long id) {
        return stockAlertRepository.findById(id);
    }

    @Transactional
    public StockAlert createAlert(AlertRequest request) {
        String normalizedSymbol = request.symbol().trim().toUpperCase(Locale.ROOT);

        StockAlert stockAlert = new StockAlert(
                normalizedSymbol,
                request.direction(),
                request.threshold(),
                request.enabled()
        );

        return stockAlertRepository.save(stockAlert);
    }

    @Transactional
    public StockAlert updateAlert(Long id, AlertRequest request) {
        StockAlert stockAlert = stockAlertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found with id: " + id));

        stockAlert.setSymbol(request.symbol().trim().toUpperCase(Locale.ROOT));
        stockAlert.setDirection(request.direction());
        stockAlert.setThreshold(request.threshold());
        stockAlert.setEnabled(request.enabled());

        return stockAlertRepository.save(stockAlert);
    }

    @Transactional
    public StockAlert toggleAlert(Long id) {
        StockAlert stockAlert = stockAlertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found with id: " + id));

        stockAlert.setEnabled(!stockAlert.isEnabled());
        return stockAlertRepository.save(stockAlert);
    }

    @Transactional
    public void deleteAlert(Long id) {
        stockAlertRepository.deleteById(id);
    }

    @Transactional
    public void refreshAlerts() {
        List<StockAlert> alerts = stockAlertRepository.findByEnabledTrue();

        for (StockAlert alert : alerts) {
            BigDecimal currentPrice = marketDataService.fetchPrice(alert.getSymbol());
            alert.setLastPrice(currentPrice);

            boolean isAboveThreshold = alert.getDirection() == AlertDirection.ABOVE && currentPrice.compareTo(alert.getThreshold()) >= 0;
            boolean isBelowThreshold = alert.getDirection() == AlertDirection.BELOW && currentPrice.compareTo(alert.getThreshold()) <= 0;

            if (isAboveThreshold || isBelowThreshold) {
                alert.setLastTriggeredMessage(
                        String.format("%s is %s %.2f (current price: %.2f)",
                                alert.getSymbol(),
                                alert.getDirection().name().toLowerCase(Locale.ROOT),
                                alert.getThreshold().doubleValue(),
                                currentPrice.doubleValue())
                );
            }

            stockAlertRepository.save(alert);
        }
    }

    @Scheduled(fixedRate = 30000)
    public void monitorSelectedStocks() {
        refreshAlerts();
    }
}
