package com.example.demo.security.jwt;

import com.example.demo.dto.JwtAuthenticationDto;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.exception.JwtAuthenticationException;
import com.example.demo.exception.UserNotAuthenticatedException;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor

public class JwtService {

    private final UserRepository userRepository;

   @Value("${jwt.token.secret}")
    private String Jwtsecret;

    public JwtAuthenticationDto generateAuthToken(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotAuthenticatedException("There is no authorized user with this email"));

         JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();

        List<String> roles = user.getRoles().stream()
                .map(RoleEntity::getName)
                .toList();

        jwtDto.setToken(generateJwtToken(email, roles));
        jwtDto.setRefreshToken(generateRefreshToken(email, roles));
        return jwtDto;
    }

    public JwtAuthenticationDto refreshBaseToken(String email, String refreshToken){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotAuthenticatedException("There is no authorized user with this email"));

        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();

        List<String> roles = user.getRoles().stream()
                .map(RoleEntity::getName)
                .toList();

        jwtDto.setToken(generateJwtToken(email, roles));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }


    private String generateJwtToken(String email, List<String> roles) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .expiration(date)
                .signWith(generateSignInKey())
                .compact();
    }

    private String generateRefreshToken(String email, List<String> roles) {
        Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .expiration(date)
                .signWith(generateSignInKey())
                .compact();
    }

    private SecretKey generateSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(Jwtsecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(generateSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
             Jwts.parser()
                    .verifyWith(generateSignInKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }  catch (ExpiredJwtException e) {
        log.debug("JWT token is expired: {}", e.getMessage());
        throw new JwtAuthenticationException("JWT token is expired", e);
      } catch (JwtException | IllegalArgumentException e) {
        log.debug("JWT token is invalid: {}", e.getMessage());
        throw new JwtAuthenticationException("JWT token is invalid", e);
      }
    }
}
