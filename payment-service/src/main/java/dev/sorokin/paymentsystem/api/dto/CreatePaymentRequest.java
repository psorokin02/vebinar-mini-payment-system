package dev.sorokin.paymentsystem.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotNull Long userId,
        @Positive @NotNull BigDecimal amount
) {
}
