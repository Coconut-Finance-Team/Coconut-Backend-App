package com.coconut.stock_app.authentication.jwt;

import com.coconut.stock_app.authentication.oauth.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";

    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(AUTHORIZATION);
        String jwt = jwtUtil.getJwtTokenFromHeader(authorizationHeader);
        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticateUser(jwt, request, response);
        }
        chain.doFilter(request, response);
    }

    private void authenticateUser(String jwt, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (jwtUtil.validateToken(jwt)) {
            String email = jwtUtil.extractUsername(jwt);
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } else {
            log.error("인증되지 않은 JWT 토큰");
        }
    }
}