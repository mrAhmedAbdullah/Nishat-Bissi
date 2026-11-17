package in.tajdar.bissi.audit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import in.tajdar.bissi.audit.dto.AuditLogRequest;
import in.tajdar.bissi.audit.dto.AuditLogResponse;
import in.tajdar.bissi.audit.entity.AuditLog;
import in.tajdar.bissi.audit.repo.AuditLogRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

	private final AuditLogRepo auditLogRepo;

	@Override
	public AuditLogResponse recordLog(AuditLogRequest request) {
		AuditLog log = AuditLog.builder()
                .action(request.action())
                .entityType(request.entityType())
                .entityId(request.entityId())
                .performedBy(request.performedBy())
                .details(request.details())
                .build();

        AuditLog saved = auditLogRepo.save(log);
        return mapToResponse(saved);
        }

	@Override
	public List<AuditLogResponse> getAllLogs() {
		return auditLogRepo.findAllByOrderByTimestampDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
	}

	@Override
	public List<AuditLogResponse> getLogsByEntityId(String entityId) {
		return auditLogRepo.findByEntityIdOrderByTimestampDesc(entityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
	}

	private AuditLogResponse mapToResponse(AuditLog log) {
		return AuditLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .performedBy(log.getPerformedBy())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
	}
}
