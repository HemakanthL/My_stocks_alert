package com.example.stockalert.dto;

import com.example.stockalert.entity.AlertDirection;
import com.example.stockalert.entity.StockAlert;

import java.math.BigDecimal;
import java.time.Instant;

public record AlertResponse(
        Long id,
        String symbol,
        AlertDirection direction,
        BigDecimal threshold,
        boolean enabled,
        BigDecimal lastPrice,
        String lastTriggeredMessage,
        Instant createdAt
) {

    public static AlertResponse fromEntity(StockAlert stockAlert) {
        return new AlertResponse(
                stockAlert.getId(),
                stockAlert.getSymbol(),
                stockAlert.getDirection(),
                stockAlert.getThreshold(),
                stockAlert.isEnabled(),
                stockAlert.getLastPrice(),
                stockAlert.getLastTriggeredMessage(),
                stockAlert.getCreatedAt()
        );
    }
}
