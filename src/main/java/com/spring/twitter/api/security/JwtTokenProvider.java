package com.spring.twitter.api.security;

import com.spring.twitter.api.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;
    @Value("${security.jwt.token.expire-length:864000000}")
    private long validityInMilliseconds; // 30 min
    private String token;

    public String getToken() {
        return token;
    }
    @Autowired
    private UserAuthDetails userAuthDetails;
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    public String createToken(String email, String password, Boolean rememberMeExpiration, Boolean isTemporary) {

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("auth", password);

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
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userAuthDetails.loadUserByUsername(getPassword(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getPassword(String token) {
        //System.out.println(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("auth"));
        return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("auth");
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            this.token = bearerToken.substring(7);
            return token;
        }
        return null;
    }
}
