package com.app.picollo.infrastructure.security;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
@EnableAutoConfiguration
public class JWTHelper {

    public static final String JWT_SECRET_KEY = "ZmxpcCBhc3NpZ25tZW50IGNyZWF0ZSB0b2tlbiA9PSBhdWxpYSByaXpxYW4=";

    public static <T> String generateToken(T request) {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        Key signingKey = new SecretKeySpec(Base64.getDecoder().decode(JWT_SECRET_KEY), 
                            SignatureAlgorithm.HS256.getJcaName());

        return Jwts.builder()
                .claim("request", request)
                .setIssuer("System")
                .setSubject("JWT API")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .signWith(signingKey)
                .compact();
    }

    // Sample method to validate and read the JWT
    public static Claims verifyJWT(String jwt) {

        // This line will throw an exception if it is not a signed JWS (as expected)
        return Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(JWT_SECRET_KEY))
                .build()
                .parseClaimsJws(jwt).getBody();
    }

}
