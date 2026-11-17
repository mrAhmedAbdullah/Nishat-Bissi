package in.tajdar.bissi.core.service;

import in.tajdar.bissi.core.dto.request.AuthResponse;
import in.tajdar.bissi.core.dto.request.LoginRequest;
import in.tajdar.bissi.core.dto.request.RegisterRequest;

public interface AuthService {
	AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
