package in.tajdar.bissi.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import in.tajdar.bissi.core.client.AuditClient;
import in.tajdar.bissi.core.dto.request.TransactionRequest;
import in.tajdar.bissi.core.dto.response.TransactionResponse;
import in.tajdar.bissi.core.entity.Diary;
import in.tajdar.bissi.core.entity.LedgerTransaction;
import in.tajdar.bissi.core.entity.User;
import in.tajdar.bissi.core.enums.DiaryStatus;
import in.tajdar.bissi.core.enums.Role;
import in.tajdar.bissi.core.enums.TransactionType;
import in.tajdar.bissi.core.exception.BusinessException;
import in.tajdar.bissi.core.repo.DiaryRepo;
import in.tajdar.bissi.core.repo.LedgerTransactionRepo;
import in.tajdar.bissi.core.repo.UserRepo;

@ExtendWith(MockitoExtension.class)
public class LedgerServiceImplTest {
	
	@Mock
    private DiaryRepo diaryRepository;

    @Mock
    private UserRepo userRepository;

    @Mock
    private LedgerTransactionRepo transactionRepository;

    @Mock
    private AuditClient auditClient;

    @InjectMocks
    private LedgerServiceImpl ledgerService;

    private User adminUser;
    private Diary approvedDiary;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(1L)
                .email("admin@bissi.com")
                .role(Role.ROLE_ADMIN)
                .build();

        approvedDiary = Diary.builder()
                .id(10L)
                .diaryNumber("BISSI-2026-TEST01")
                .status(DiaryStatus.APPROVED)
                .currentBalance(new BigDecimal("5000.00"))
                .build();
    }

    @Test
    void recordDeposit_ShouldIncreaseBalanceAndCallAudit() {
        TransactionRequest request = new TransactionRequest(
                "BISSI-2026-TEST01", TransactionType.DEPOSIT, new BigDecimal("2000.00"), "Monthly deposit"
        );

        when(diaryRepository.findByDiaryNumber("BISSI-2026-TEST01")).thenReturn(Optional.of(approvedDiary));
        when(userRepository.findByEmail("admin@bissi.com")).thenReturn(Optional.of(adminUser));
        when(transactionRepository.save(any(LedgerTransaction.class))).thenAnswer(i -> i.getArgument(0));

        TransactionResponse response = ledgerService.recordTransaction(request, "admin@bissi.com");

        assertEquals(new BigDecimal("7000.00"), response.balanceAfter());
        assertEquals(new BigDecimal("7000.00"), approvedDiary.getCurrentBalance());
        verify(auditClient, times(1)).sendAuditLog(any());
    }

    @Test
    void recordWithdrawal_WhenInsufficientBalance_ShouldThrowBusinessException() {
        TransactionRequest request = new TransactionRequest(
                "BISSI-2026-TEST01", TransactionType.WITHDRAWAL, new BigDecimal("6000.00"), "Overdraw test"
        );

        when(diaryRepository.findByDiaryNumber("BISSI-2026-TEST01")).thenReturn(Optional.of(approvedDiary));
        when(userRepository.findByEmail("admin@bissi.com")).thenReturn(Optional.of(adminUser));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> ledgerService.recordTransaction(request, "admin@bissi.com"));

        assertTrue(ex.getMessage().contains("Insufficient balance"));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void recordTransaction_WhenDiaryNotApproved_ShouldThrowBusinessException() {
        approvedDiary.setStatus(DiaryStatus.PENDING);
        TransactionRequest request = new TransactionRequest(
                "BISSI-2026-TEST01", TransactionType.DEPOSIT, new BigDecimal("1000.00"), "Unapproved test"
        );

        when(diaryRepository.findByDiaryNumber("BISSI-2026-TEST01")).thenReturn(Optional.of(approvedDiary));

        assertThrows(BusinessException.class,
                () -> ledgerService.recordTransaction(request, "admin@bissi.com"));
    }
}
