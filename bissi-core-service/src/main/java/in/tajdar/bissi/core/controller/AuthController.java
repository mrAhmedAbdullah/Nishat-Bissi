package in.tajdar.bissi.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.tajdar.bissi.core.dto.request.AuthResponse;
import in.tajdar.bissi.core.dto.request.LoginRequest;
import in.tajdar.bissi.core.dto.request.RegisterRequest;
import in.tajdar.bissi.core.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	    private final AuthService authService;

	    @PostMapping("/register")
	    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
	        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
	    }

	    @PostMapping("/login")
	    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
	        return ResponseEntity.ok(authService.login(request));
	    }
}
