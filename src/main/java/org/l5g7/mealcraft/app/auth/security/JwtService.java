package org.l5g7.mealcraft.app.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.l5g7.mealcraft.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private SecretKey secretKey;

    public JwtService(JwtKey jwtKey){
        this.secretKey = jwtKey.getSecretKey();
    }

    @Value("${jwt.expiration-ms}")
    private long expirationMs; // 1 day

    public String generateToken(String username, Role role) {
        Map<String, String> claims = new HashMap<>();
        claims.put("role", String.valueOf(role));
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (ExpiredJwtException | MalformedJwtException e) {
            return null;
        }
    }

    public String getRolesFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("role").toString();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        return getUsernameFromToken(token) != null;
    }
}
