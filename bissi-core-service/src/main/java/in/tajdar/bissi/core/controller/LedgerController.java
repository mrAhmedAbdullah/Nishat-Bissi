package in.tajdar.bissi.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.tajdar.bissi.core.dto.request.TransactionRequest;
import in.tajdar.bissi.core.dto.response.TransactionResponse;
import in.tajdar.bissi.core.service.LedgerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@AllArgsConstructor
public class LedgerController {

	@Autowired
	private LedgerService ledgerService;
	
	@PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> recordTransaction(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication
    ) {
        return new ResponseEntity<>(
                ledgerService.recordTransaction(request, authentication.getName()),
                HttpStatus.CREATED
        );
    }
	@GetMapping("/diary/{diaryNumber}")
    public ResponseEntity<List<TransactionResponse>> getDiaryStatement(
            @PathVariable String diaryNumber,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
		return ResponseEntity.ok(ledgerService.getDiaryStatement(diaryNumber, authentication.getName(), isAdmin));
	}
}
