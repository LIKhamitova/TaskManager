package com.example.demo.security.jwt;

import com.example.demo.dto.JwtAuthenticationDTO;
import com.example.demo.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtService {

    @Value("${jwt.token.secret}")
    private String Jwtsecret;

    @Value("${jwt.token.expired}")
    private long validityInMilliseconds;

    //Получение токена по email
    public JwtAuthenticationDTO generateAuthToken(String email){
        JwtAuthenticationDTO jwtDto = new JwtAuthenticationDTO();
        jwtDto.setToken(generateJwtToken(email));
    }

    private String generateJwtToken(String email) {
      return Jwts.builder()
                .subject(email)
//                .claim("roles", getRoleNames(roles))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
                .signWith(generateSingInKey())
                .compact();
    }
    //Base64 декодирование секретной фразы
    //Возвращает SecretKey для использования с HS256
    private SecretKey generateSingInKey(){
           byte[] keyBytes = Decoders.BASE64.decode(Jwtsecret);
           return Keys.hmacShaKeyFor(keyBytes);
    }

    //Достаем email из токена
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(generateSingInKey())
                .build()
                .parseSignedClaims(token)
               .getPayload()
                .getSubject();
    }
    //Проверка токена на валидность
       public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(generateSingInKey())
                    .build()
                    .parseSignedClaims(token)
                    ;

            return !claims.getPayload().getExpiration().before(new Date()); //доп. не истек ли срок действия токена
        } catch (ExpiredJwtException e) {
            log.debug("JWT token is expired: {}", e.getMessage());
        throw new JwtAuthenticationException("JWT token is expired", e);
    } catch (MalformedJwtException e) {
            log.debug("JWT token is malformed: {}", e.getMessage());
        throw new JwtAuthenticationException("JWT token is malformed", e);
    } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT token is invalid: {}", e.getMessage());
        throw new JwtAuthenticationException("JWT token is invalid", e);
    }
        }
    }

