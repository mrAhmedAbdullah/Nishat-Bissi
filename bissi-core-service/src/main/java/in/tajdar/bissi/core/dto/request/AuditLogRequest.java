package in.tajdar.bissi.core.dto.request;

import lombok.Builder;

@Builder
public record AuditLogRequest(
		String action,
        String entityType,
        String entityId,
        String performedBy,
        String details) {

}
