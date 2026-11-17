package in.tajdar.bissi.audit.dto;

import java.time.LocalDateTime;

import lombok.Builder;
@Builder
public record AuditLogResponse(Long id, String action, String entityType, String entityId, String performedBy,
		String details, LocalDateTime timestamp) {

}
