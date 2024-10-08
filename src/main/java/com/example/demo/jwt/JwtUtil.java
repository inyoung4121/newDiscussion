package com.example.demo.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import jakarta.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private Key getSigningKey() {
        String secret = "gasfegqwdasdgasfegqwdasdgasfegqwdasdgasfegqwdasdgasfegqwdasqwdasdgasfegqwdasdgasfegqwdasqwdasdgasfegqwdasdgasfegqwdasd";
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static final long REFRESH_TOKEN_EXPIRATION_MS = 604800000; // 1주일

    //리프래쉬 토큰 생성
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    //리프래쉬 토큰으로 재발급
    public String generateNewAccessToken(String refreshToken) {
        String email = getEmailFromToken(refreshToken);
        return generateToken(email);
    }



    // 토큰에서 사용자 이메일 추출
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // 토큰에서 만료 시간 추출
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // 토큰에서 특정 클레임 추출
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 모든 클레임 추출
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()  // JwtParserBuilder에서 빌드
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        String token = null;

        // 1. Try to extract from header
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
            logger.debug("Token extracted from Authorization header");
            return token;
        }

        // 2. If not in header, try to extract from cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("stockJwtToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    logger.debug("Token extracted from cookie");
                    return token;
                }
            }
        }

        // 3. If token is still null, log the failure
        if (token == null) {
            logger.warn("No token found in request (neither in header nor in cookies)");
        }

        return token;
    }
    // 토큰 만료 여부 확인
    public Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    // 토큰 생성
    public String generateToken(String email) {
        return doGenerateToken(Map.of(), email);
    }

    // 토큰 생성 내부 메소드
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        long jwtExpirationInMs = 3600000;
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // 토큰 유효성 검증
    public Boolean validateToken(String token, String email) {
        final String extractedEmail = getEmailFromToken(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }
}
