package in.tajdar.bissi.audit.service;

import java.util.List;

import in.tajdar.bissi.audit.dto.AuditLogRequest;
import in.tajdar.bissi.audit.dto.AuditLogResponse;

public interface AuditLogService {
	AuditLogResponse recordLog(AuditLogRequest request);
    List<AuditLogResponse> getAllLogs();
    List<AuditLogResponse> getLogsByEntityId(String entityId);
}
