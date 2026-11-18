package in.tajdar.bissi.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.tajdar.bissi.core.dto.request.DiaryApprovalRequest;
import in.tajdar.bissi.core.dto.response.DiaryResponse;
import in.tajdar.bissi.core.enums.DiaryStatus;
import in.tajdar.bissi.core.service.DiaryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/diaries")
@RequiredArgsConstructor
@AllArgsConstructor
public class DiaryController {

	@Autowired
	private DiaryService diaryService;

	@GetMapping("/my")
	@PreAuthorize("hasRole('MEMBER')")
	public ResponseEntity<DiaryResponse> getMyDiary(Authentication authentication) {
		return ResponseEntity.ok(diaryService.getMyDiary(authentication.getName()));
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<DiaryResponse>> getDiaries(@RequestParam(required = false) DiaryStatus status) {
		if (status != null) {
			return ResponseEntity.ok(diaryService.getDiariesByStatus(status));
		}
		return ResponseEntity.ok(diaryService.getAllDiaries());
	}

	@PatchMapping("/{diaryNumber}/review")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<DiaryResponse> reviewDiary(@PathVariable String diaryNumber,
			@Valid @RequestBody DiaryApprovalRequest request, Authentication authentication) {
		return ResponseEntity.ok(diaryService.reviewDiary(diaryNumber, request, authentication.getName()));
	}
}
