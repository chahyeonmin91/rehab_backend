package com.rehab.service.authService;

import com.rehab.dto.auth.AuthRequest;
import com.rehab.dto.auth.AuthResponse;

public interface AuthService {

	AuthResponse.SignupResponse signup(AuthRequest.SignupRequest request);

	AuthResponse.LoginResponse login(AuthRequest.LoginRequest request);
}
