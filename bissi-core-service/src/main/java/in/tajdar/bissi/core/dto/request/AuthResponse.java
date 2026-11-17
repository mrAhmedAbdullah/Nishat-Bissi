package in.tajdar.bissi.core.dto.request;

import lombok.Builder;

@Builder
public record AuthResponse(String token, Long userId, String fullName, String email, String role, String diaryNumber,
		String diaryStatus) {
}
