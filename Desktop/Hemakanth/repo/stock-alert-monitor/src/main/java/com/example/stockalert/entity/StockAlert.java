package com.example.stockalert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "stock_alerts")
public class StockAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertDirection direction;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal threshold;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal lastPrice = BigDecimal.ZERO;

    @Column
    private String lastTriggeredMessage;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected StockAlert() {
    }

    public StockAlert(String symbol, AlertDirection direction, BigDecimal threshold, boolean enabled) {
        this.symbol = symbol.trim().toUpperCase();
        this.direction = direction;
        this.threshold = threshold;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol.trim().toUpperCase();
    }

    public AlertDirection getDirection() {
        return direction;
    }

    public void setDirection(AlertDirection direction) {
        this.direction = direction;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getLastTriggeredMessage() {
        return lastTriggeredMessage;
    }

    public void setLastTriggeredMessage(String lastTriggeredMessage) {
        this.lastTriggeredMessage = lastTriggeredMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
