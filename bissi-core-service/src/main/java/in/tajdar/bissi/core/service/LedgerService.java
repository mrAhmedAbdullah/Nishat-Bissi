package in.tajdar.bissi.core.service;

import java.util.List;

import in.tajdar.bissi.core.dto.request.TransactionRequest;
import in.tajdar.bissi.core.dto.response.TransactionResponse;

public interface LedgerService {
	TransactionResponse recordTransaction(TransactionRequest request, String adminEmail);
    List<TransactionResponse> getDiaryStatement(String diaryNumber, String requesterEmail, boolean isAdmin);
}
