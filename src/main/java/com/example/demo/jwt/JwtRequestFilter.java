package com.example.demo.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtTokenUtil;
    private final UserDetailServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        final Cookie[] cookies = request.getCookies();
        final String refreshToken = cookies != null ?
                Arrays.stream(cookies)
                        .filter(c -> c.getName().equals("refreshToken"))
                        .findFirst()
                        .map(Cookie::getValue)
                        .orElse(null) : null;

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getEmailFromToken(jwtToken);
            } catch (ExpiredJwtException e) {
                logger.info("JWT Token has expired");
                if (refreshToken != null) {
                    try {
                        // 리프레시 토큰 유효성 검사
                        if (!jwtTokenUtil.isTokenExpired(refreshToken)) {
                            // 새로운 액세스 토큰 생성
                            jwtToken = jwtTokenUtil.generateNewAccessToken(refreshToken);
                            username = jwtTokenUtil.getEmailFromToken(jwtToken);
                            // 새로운 JWT 토큰을 응답 헤더에 추가
                            response.setHeader("Authorization", "Bearer " + jwtToken);
                            // 클라이언트에게 토큰이 갱신되었음을 알림
                            response.setHeader("Token-Refreshed", "true");
                            logger.info("New access token generated from refresh token");
                        } else {
                            logger.error("Refresh token is invalid");
                            // 리프레시 토큰이 유효하지 않으면 쿠키에서 제거
                            Cookie refreshTokenCookie = new Cookie("refreshToken", null);
                            refreshTokenCookie.setMaxAge(0);
                            refreshTokenCookie.setPath("/");
                            response.addCookie(refreshTokenCookie);
                        }
                    } catch (Exception refreshException) {
                        logger.error("Error while refreshing token", refreshException);
                    }
                }
            } catch (Exception e) {
                logger.error("Unable to get JWT Token", e);
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}