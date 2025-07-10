package com.auth.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Util class to generate and validate JWT token
 */

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expiration;
    private JwtParser parser;

    public JwtUtil(@Value("${jwt.secret}") String secret , @Value("${jwt.expiration}") long expiration){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    @PostConstruct
    public void initParser(){
        this.parser = Jwts.parser()
                .verifyWith(key)
                .json(new JacksonDeserializer<>())
                .build();
    }

    public String generateToken(String email , String role){
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .serializeToJsonWith(new JacksonSerializer<>())
                .compact();
    }
    public boolean validateToken(String token){
        try {
            parser.parseSignedClaims(token);
            return true;
        } catch (JwtException ex){
            return false;
        }
    }
    public String extractUsername(String token){
        return parser.parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    public String extractRole(String token){
        return parser.parseSignedClaims(token)
                .getPayload()
                .get("role" , String.class);
    }
}
