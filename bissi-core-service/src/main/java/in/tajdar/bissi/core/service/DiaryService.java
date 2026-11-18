package in.tajdar.bissi.core.service;

import java.util.List;

import in.tajdar.bissi.core.dto.request.DiaryApprovalRequest;
import in.tajdar.bissi.core.dto.response.DiaryResponse;
import in.tajdar.bissi.core.enums.DiaryStatus;

public interface DiaryService {
	List<DiaryResponse> getDiariesByStatus(DiaryStatus status);
    List<DiaryResponse> getAllDiaries();
    DiaryResponse getMyDiary(String memberEmail);
    DiaryResponse reviewDiary(String diaryNumber, DiaryApprovalRequest request, String adminEmail);
}
