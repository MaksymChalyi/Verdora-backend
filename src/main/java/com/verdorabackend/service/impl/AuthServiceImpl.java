package com.verdorabackend.service.impl;

import com.verdorabackend.dto.request.SignUpRequest;
import com.verdorabackend.dto.response.SignupResponse;
import com.verdorabackend.entity.Role;
import com.verdorabackend.entity.User;
import com.verdorabackend.mapper.UserMapper;
import com.verdorabackend.repository.UserRepository;
import com.verdorabackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SignupResponse signup(SignUpRequest request) {
        log.debug("Attempting to sign up user with email: {}", request.email());
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Signup failed: email already exists, email={}", request.email());
            throw new RuntimeException("Email already exists.");
        }
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        userRepository.save(user);
        log.info("User registered successfully: email={}, id={}", user.getEmail(), user.getId());

        return new SignupResponse(user.getEmail());
    }


}
