package com.verdorabackend.service.impl;

import com.verdorabackend.dto.auth.AuthResult;
import com.verdorabackend.dto.request.SignInRequest;
import com.verdorabackend.dto.request.SignUpRequest;
import com.verdorabackend.entity.Role;
import com.verdorabackend.entity.User;
import com.verdorabackend.exception.InvalidCredentialsException;
import com.verdorabackend.exception.InvalidTokenException;
import com.verdorabackend.exception.UserAlreadyExistsException;
import com.verdorabackend.exception.WrongTokenTypeException;
import com.verdorabackend.mapper.UserMapper;
import com.verdorabackend.repository.UserRepository;
import com.verdorabackend.security.CustomUserDetailsService;
import com.verdorabackend.security.JwtService;
import com.verdorabackend.security.UserPrincipal;
import com.verdorabackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    @Transactional
    public AuthResult signup(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Signup failed: email already exists, email={}", request.email());
            throw new UserAlreadyExistsException();
        }
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        userRepository.save(user);
        log.info("User registered successfully: email={}, id={}", user.getEmail(), user.getId());
        UserPrincipal principal = new UserPrincipal(user);
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);
        return new AuthResult(user.getEmail(), accessToken, refreshToken);
    }

    @Override
    public AuthResult login(SignInRequest request) {

        // Authenticate user using Spring Security:
        // - delegates to AuthenticationManager
        // - loads user via CustomUserDetailsService (by email)
        // - compares raw password with stored hash using PasswordEncoder
        // - throws exception if credentials are invalid
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user =
                userRepository
                        .findUserByEmail(request.email())
                        .orElseThrow(InvalidCredentialsException::new);
        String accessToken = jwtService.generateAccessToken(new UserPrincipal(user));
        String refreshToken = jwtService.generateRefreshToken(new UserPrincipal(user));
        log.info("User logged in: userId={}, email={}", user.getId(), user.getEmail());
        return new AuthResult(user.getEmail(), accessToken, refreshToken);
    }

    @Override
    public String refresh(String refreshToken) {

        if (refreshToken == null) {
            throw new InvalidTokenException();
        }
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new WrongTokenTypeException();
        }

        String email = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
            throw new InvalidTokenException();
        }

        return jwtService.generateAccessToken(userDetails);
    }

    @Override
    @Transactional
    public AuthResult loginOrRegisterGoogleUser(String email, String name) {
        User user =
                userRepository
                        .findUserByEmail(email)
                        .orElseGet(
                                () -> {
                                    User newUser = new User();
                                    newUser.setEmail(email);
                                    newUser.setName(name);
                                    newUser.setRole(Role.USER);
                                    newUser.setPasswordHash("GOOGLE_OAUTH2_USER");
                                    return userRepository.save(newUser);
                                });

        UserPrincipal principal = new UserPrincipal(user);

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        return new AuthResult(user.getEmail(), accessToken, refreshToken);
    }
}
