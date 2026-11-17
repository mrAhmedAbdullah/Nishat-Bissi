package in.tajdar.bissi.audit.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.tajdar.bissi.audit.dto.AuditLogRequest;
import in.tajdar.bissi.audit.dto.AuditLogResponse;
import in.tajdar.bissi.audit.service.AuditLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/audits")
public class AuditLogController {
	private final AuditLogService auditLogService;

    @PostMapping
    public ResponseEntity<AuditLogResponse> createAuditLog(@Valid @RequestBody AuditLogRequest request) {
        return new ResponseEntity<>(auditLogService.recordLog(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/entity/{entityId}")
    public ResponseEntity<List<AuditLogResponse>> getLogsByEntity(@PathVariable String entityId) {
        return ResponseEntity.ok(auditLogService.getLogsByEntityId(entityId));
    }
}
