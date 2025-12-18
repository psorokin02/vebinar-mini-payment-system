package dev.sorokin.paymentsystem.api.dto;

import java.math.BigDecimal;

// TODO: добавить валидацию nonnull + positive
public record CreatePaymentRequest(
        Long userId,
        BigDecimal amount
) {
}
