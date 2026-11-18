package in.tajdar.bissi.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import in.tajdar.bissi.core.dto.request.AuditLogRequest;

@FeignClient(name = "bissi-audit-service", url = "${services.audit-service.url}")
public interface AuditClient {
	
	@PostMapping("/api/v1/audits")
	void sendAuditLog(@RequestBody AuditLogRequest request);
}
