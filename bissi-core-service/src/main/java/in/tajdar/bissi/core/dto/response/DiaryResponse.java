package in.tajdar.bissi.core.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record DiaryResponse(Long id,
        String diaryNumber,
        Long memberId,
        String memberName,
        String memberEmail,
        String status,
        BigDecimal currentBalance,
        String approvedByEmail,
        String rejectionReason,
        LocalDateTime approvedAt,
        LocalDateTime createdAt) {

}
