package dev.sorokin.paymentsystem.domain;

import dev.sorokin.paymentsystem.api.dto.PaymentDto;
import dev.sorokin.paymentsystem.domain.db.PaymentEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentEntityMapper {

    public PaymentDto convertEntityToDto(PaymentEntity payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    public List<PaymentDto> mapListToDto(List<PaymentEntity> payments) {
        return payments.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }
}
