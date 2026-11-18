package in.tajdar.bissi.core.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.tajdar.bissi.core.client.AuditClient;
import in.tajdar.bissi.core.dto.request.AuditLogRequest;
import in.tajdar.bissi.core.dto.request.DiaryApprovalRequest;
import in.tajdar.bissi.core.dto.response.DiaryResponse;
import in.tajdar.bissi.core.entity.Diary;
import in.tajdar.bissi.core.entity.User;
import in.tajdar.bissi.core.enums.DiaryStatus;
import in.tajdar.bissi.core.exception.BusinessException;
import in.tajdar.bissi.core.exception.ResourceNotFoundException;
import in.tajdar.bissi.core.repo.DiaryRepo;
import in.tajdar.bissi.core.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepo diaryRepository;
    private final UserRepo userRepository;
    private final AuditClient auditClient;

    @Override
    public List<DiaryResponse> getDiariesByStatus(DiaryStatus status) {
        return diaryRepository.findByStatus(status).stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<DiaryResponse> getAllDiaries() {
        return diaryRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public DiaryResponse getMyDiary(String memberEmail) {
        User member = userRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        Diary diary = diaryRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No diary linked to this member"));
        return mapToResponse(diary);
    }

    @Override
    @Transactional
    public DiaryResponse reviewDiary(String diaryNumber, DiaryApprovalRequest request, String adminEmail) {
        if (request.status() != DiaryStatus.APPROVED && request.status() != DiaryStatus.REJECTED) {
            throw new BusinessException("Review status must be either APPROVED or REJECTED");
        }

        Diary diary = diaryRepository.findByDiaryNumber(diaryNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Diary not found: " + diaryNumber));

        if (diary.getStatus() != DiaryStatus.PENDING) {
            throw new BusinessException("Only PENDING diaries can be reviewed. Current status: " + diary.getStatus());
        }

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        diary.setStatus(request.status());
        diary.setApprovedBy(admin);

        if (request.status() == DiaryStatus.APPROVED) {
            diary.setApprovedAt(LocalDateTime.now());
            diary.setRejectionReason(null);
        } else {
            if (request.rejectionReason() == null || request.rejectionReason().isBlank()) {
                throw new BusinessException("Rejection reason is mandatory when rejecting a diary");
            }
            diary.setRejectionReason(request.rejectionReason());
        }

        Diary updatedDiary = diaryRepository.save(diary);

        // Inter-service Feign call to bissi-audit-service
        sendAuditSafe(AuditLogRequest.builder()
                .action("DIARY_" + request.status().name())
                .entityType("DIARY")
                .entityId(updatedDiary.getDiaryNumber())
                .performedBy(adminEmail)
                .details("Diary status changed to " + request.status().name()
                        + (request.rejectionReason() != null ? " | Reason: " + request.rejectionReason() : ""))
                .build());

        return mapToResponse(updatedDiary);
    }

    private void sendAuditSafe(AuditLogRequest auditRequest) {
        try {
            auditClient.sendAuditLog(auditRequest);
        } catch (Exception ex) {
            log.error("Failed to push audit log to audit-service: {}", ex.getMessage());
        }
    }

    private DiaryResponse mapToResponse(Diary diary) {
        return DiaryResponse.builder()
                .id(diary.getId())
                .diaryNumber(diary.getDiaryNumber())
                .memberId(diary.getMember().getId())
                .memberName(diary.getMember().getFullName())
                .memberEmail(diary.getMember().getEmail())
                .status(diary.getStatus().name())
                .currentBalance(diary.getCurrentBalance())
                .approvedByEmail(diary.getApprovedBy() != null ? diary.getApprovedBy().getEmail() : null)
                .rejectionReason(diary.getRejectionReason())
                .approvedAt(diary.getApprovedAt())
                .createdAt(diary.getCreatedAt())
                .build();
    }
}