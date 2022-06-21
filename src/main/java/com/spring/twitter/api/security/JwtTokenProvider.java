package com.spring.twitter.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;
    @Value("${security.jwt.token.expire-length:864000000}")
    private long validityInMilliseconds; // 30 min
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    public String createToken(String email, String name, Boolean rememberMeExpiration, Boolean isTemporary) {

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("auth", name);

        if(isTemporary) {
            validityInMilliseconds = Long.parseUnsignedLong("3600000");
        } else {
            validityInMilliseconds = Long.parseUnsignedLong("2592000000");
        }

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()//
                .setClaims(claims)//
                .setIssuedAt(now)//
                .setExpiration(validity)//
                .signWith(SignatureAlgorithm.HS256, secretKey)//
                .compact();
    }

}
