package com.example.stockalert.dto;

import com.example.stockalert.entity.AlertDirection;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AlertRequest(
        @NotBlank(message = "Symbol is required") String symbol,
        @NotNull(message = "Direction is required") AlertDirection direction,
        @NotNull(message = "Threshold is required")
        @DecimalMin(value = "0.01", message = "Threshold must be greater than zero") BigDecimal threshold,
        boolean enabled
) {
}
