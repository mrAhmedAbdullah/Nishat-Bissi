package in.tajdar.bissi.core.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.tajdar.bissi.core.client.AuditClient;
import in.tajdar.bissi.core.dto.request.AuditLogRequest;
import in.tajdar.bissi.core.dto.request.TransactionRequest;
import in.tajdar.bissi.core.dto.response.TransactionResponse;
import in.tajdar.bissi.core.entity.Diary;
import in.tajdar.bissi.core.entity.LedgerTransaction;
import in.tajdar.bissi.core.entity.User;
import in.tajdar.bissi.core.enums.DiaryStatus;
import in.tajdar.bissi.core.enums.TransactionType;
import in.tajdar.bissi.core.exception.BusinessException;
import in.tajdar.bissi.core.exception.ResourceNotFoundException;
import in.tajdar.bissi.core.repo.DiaryRepo;
import in.tajdar.bissi.core.repo.LedgerTransactionRepo;
import in.tajdar.bissi.core.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequiredArgsConstructor
@Service
public class LedgerServiceImpl implements LedgerService{
	private final DiaryRepo diaryRepository;
    private final UserRepo userRepository;
    private final LedgerTransactionRepo transactionRepository;
    private final AuditClient auditClient;

    @Override
    @Transactional
    public TransactionResponse recordTransaction(TransactionRequest request, String adminEmail) {
        Diary diary = diaryRepository.findByDiaryNumber(request.diaryNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Diary not found: " + request.diaryNumber()));

        if (diary.getStatus() != DiaryStatus.APPROVED) {
            throw new BusinessException("Transactions are only allowed on APPROVED diaries. Current status: " + diary.getStatus());
        }

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        BigDecimal newBalance;
        if (request.type() == TransactionType.DEPOSIT) {
            newBalance = diary.getCurrentBalance().add(request.amount());
        } else {
            if (diary.getCurrentBalance().compareTo(request.amount()) < 0) {
                throw new BusinessException("Insufficient balance in diary. Available: ₹" + diary.getCurrentBalance());
            }
            newBalance = diary.getCurrentBalance().subtract(request.amount());
        }

        diary.setCurrentBalance(newBalance);
        diaryRepository.save(diary);

        String refNumber = "TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        LedgerTransaction txn = LedgerTransaction.builder()
                .referenceNumber(refNumber)
                .diary(diary)
                .type(request.type())
                .amount(request.amount())
                .balanceAfter(newBalance)
                .remarks(request.remarks())
                .recordedBy(admin)
                .build();

        LedgerTransaction savedTxn = transactionRepository.save(txn);

        // Inter-service Feign call to bissi-audit-service
        try {
            auditClient.sendAuditLog(AuditLogRequest.builder()
                    .action(request.type().name() + "_RECORDED")
                    .entityType("TRANSACTION")
                    .entityId(refNumber)
                    .performedBy(adminEmail)
                    .details("Diary: " + diary.getDiaryNumber() + " | Amount: ₹" + request.amount() + " | New Balance: ₹" + newBalance)
                    .build());
        } catch (Exception ex) {
            log.error("Audit log failed for txn {}: {}", refNumber, ex.getMessage());
        }

        return mapToResponse(savedTxn);
    }

    @Override
    public List<TransactionResponse> getDiaryStatement(String diaryNumber, String requesterEmail, boolean isAdmin) {
        Diary diary = diaryRepository.findByDiaryNumber(diaryNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Diary not found: " + diaryNumber));

        if (!isAdmin && !diary.getMember().getEmail().equals(requesterEmail)) {
            throw new BusinessException("Access denied: You can only view your own diary statement");
        }

        return transactionRepository.findByDiaryIdOrderByTransactionDateDesc(diary.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TransactionResponse mapToResponse(LedgerTransaction txn) {
        return TransactionResponse.builder()
                .id(txn.getId())
                .referenceNumber(txn.getReferenceNumber())
                .diaryNumber(txn.getDiary().getDiaryNumber())
                .type(txn.getType().name())
                .amount(txn.getAmount())
                .balanceAfter(txn.getBalanceAfter())
                .remarks(txn.getRemarks())
                .recordedByEmail(txn.getRecordedBy().getEmail())
                .transactionDate(txn.getTransactionDate())
                .build();
}
}
