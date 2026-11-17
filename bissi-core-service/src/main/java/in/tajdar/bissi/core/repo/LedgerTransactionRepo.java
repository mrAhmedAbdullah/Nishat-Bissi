package in.tajdar.bissi.core.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.tajdar.bissi.core.entity.LedgerTransaction;

public interface LedgerTransactionRepo extends JpaRepository<LedgerTransaction, Long> {
	List<LedgerTransaction> findByDiaryIdOrderByTransactionDateDesc(Long diaryId);
}
