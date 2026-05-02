package com.verdorabackend.security;

import com.verdorabackend.exception.InvalidTokenException;
import com.verdorabackend.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractTokenType(token));
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpirationMs()))
                .signWith(getSecretKey())
                .compact();
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        Claims claims = getValidatedClaims(token);
        return userDetails.getUsername().equals(claims.getSubject())
                && "refresh".equals(claims.get("type", String.class));
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        Claims claims = getValidatedClaims(token);
        return userDetails.getUsername().equals(claims.getSubject())
                && "access".equals(claims.get("type", String.class));
    }

    private Claims getValidatedClaims(String token) {
        return extractAllClaims(token);
    }

    public boolean isAccessToken(String token) {
        return "access".equals(extractTokenType(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String extractTokenType(String token) {
        return extractAllClaims(token).get("type", String.class);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("type", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessExpirationMs()))
                .signWith(getSecretKey())
                .compact();
    }

    // Key for sign JWT
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

}
