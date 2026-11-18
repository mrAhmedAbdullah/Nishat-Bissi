package in.tajdar.bissi.core.dto.request;

import in.tajdar.bissi.core.enums.DiaryStatus;
import jakarta.validation.constraints.NotNull;

public record DiaryApprovalRequest(@NotNull(message = "Status is required (APPROVED or REJECTED)")
DiaryStatus status,
String rejectionReason) {

}
