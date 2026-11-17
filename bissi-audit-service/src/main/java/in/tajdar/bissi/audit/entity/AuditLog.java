package in.tajdar.bissi.audit.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String action; // e.g., DIARY_APPROVED, DIARY_REJECTED, DEPOSIT_RECORDED, WITHDRAWAL_RECORDED

    @Column(nullable = false, length = 50)
    private String entityType; // e.g., DIARY, TRANSACTION

    @Column(nullable = false, length = 50)
    private String entityId; // e.g., BISSI-2026-XXXXXX or TXN Reference

    @Column(nullable = false, length = 120)
    private String performedBy; // Admin/User email or ID

    @Column(length = 500)
    private String details;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;
}
