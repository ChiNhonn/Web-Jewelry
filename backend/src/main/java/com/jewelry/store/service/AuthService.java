package com.jewelry.store.service;

import com.jewelry.store.dto.request.LoginRequest;
import com.jewelry.store.dto.request.RefreshTokenRequest;
import com.jewelry.store.dto.request.RegisterRequest;
import com.jewelry.store.dto.response.AuthResponse;
import com.jewelry.store.dto.response.UserSummaryResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String refreshToken);

    UserSummaryResponse getCurrentUserProfile(String username);
}
