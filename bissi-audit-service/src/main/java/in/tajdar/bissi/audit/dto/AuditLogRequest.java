package in.tajdar.bissi.audit.dto;

import jakarta.validation.constraints.NotBlank;

public record AuditLogRequest(
		@NotBlank(message = "Action is required") String action,

		@NotBlank(message = "Entity type is required") String entityType,

		@NotBlank(message = "Entity ID is required") String entityId,

		@NotBlank(message = "PerformedBy is required") String performedBy,

		String details) {

}
