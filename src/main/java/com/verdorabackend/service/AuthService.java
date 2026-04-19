package com.verdorabackend.service;

import com.verdorabackend.dto.request.SignUpRequest;
import com.verdorabackend.dto.response.SignInResponse;
import com.verdorabackend.dto.request.SignInRequest;
import com.verdorabackend.dto.response.SignupResponse;

public interface AuthService {

    SignupResponse signup(SignUpRequest signupRequest);
    SignInResponse login(SignInRequest signupRequest);
}
