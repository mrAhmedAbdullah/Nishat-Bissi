package in.tajdar.bissi.audit.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.tajdar.bissi.audit.entity.AuditLog;

public interface AuditLogRepo extends JpaRepository<AuditLog, Long> {
	List<AuditLog> findAllByOrderByTimestampDesc();
    List<AuditLog> findByEntityIdOrderByTimestampDesc(String entityId);
}
