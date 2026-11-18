package in.tajdar.bissi.core.dto.request;

import java.math.BigDecimal;

import in.tajdar.bissi.core.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransactionRequest(
		@NotBlank(message = "Diary number is required") String diaryNumber,

		@NotNull(message = "Transaction type is required (DEPOSIT or WITHDRAWAL)") TransactionType type,

		@NotNull(message = "Amount is required") @DecimalMin(value = "1.00", message = "Amount must be at least 1.00") BigDecimal amount,

		String remarks) {

}
