package com.verdorabackend.service;

import com.verdorabackend.dto.auth.AuthResult;
import com.verdorabackend.dto.request.SignInRequest;
import com.verdorabackend.dto.request.SignUpRequest;
import com.verdorabackend.dto.response.SignupResponse;

public interface AuthService {

    SignupResponse signup(SignUpRequest signupRequest);

    AuthResult login(SignInRequest signupRequest);
}
