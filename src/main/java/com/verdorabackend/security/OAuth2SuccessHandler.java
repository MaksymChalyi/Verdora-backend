package com.verdorabackend.security;

import com.verdorabackend.dto.auth.AuthResult;
import com.verdorabackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final CookieService cookieService;
    private final OAuth2Properties oAuth2Properties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        AuthResult result = authService.loginOrRegisterGoogleUser(email, name);

        cookieService.addAccessToken(response, result.accessToken());
        cookieService.addRefreshToken(response, result.refreshToken());

        response.sendRedirect(oAuth2Properties.getRedirectUri());
    }
}
