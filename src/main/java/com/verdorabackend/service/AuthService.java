package com.verdorabackend.service;

import com.verdorabackend.dto.auth.AuthResult;
import com.verdorabackend.dto.request.SignInRequest;
import com.verdorabackend.dto.request.SignUpRequest;

public interface AuthService {

    AuthResult signup(SignUpRequest signupRequest);

    AuthResult login(SignInRequest signupRequest);

    String refresh(String refreshToken);
}
