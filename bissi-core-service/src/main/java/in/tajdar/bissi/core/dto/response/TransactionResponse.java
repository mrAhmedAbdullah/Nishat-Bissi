package in.tajdar.bissi.core.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
@Builder
public record TransactionResponse(
		Long id,
        String referenceNumber,
        String diaryNumber,
        String type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String remarks,
        String recordedByEmail,
        LocalDateTime transactionDate
) {}
