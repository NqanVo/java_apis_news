package com.javaspring.blogapi.config.jwt;

import com.javaspring.blogapi.config.CustomUserDetailsService;
import com.javaspring.blogapi.model.UserEntity;
import com.javaspring.blogapi.service.impl.RoleService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter2 extends OncePerRequestFilter {
    @Autowired
    private JwtService2 jwtService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String headerAuthorization = request.getHeader("Authorization");
        if (!hasAuthorizationBearer(headerAuthorization)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = headerAuthorization.split(" ")[1].trim();
        if (!jwtService.validateAccessToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        setAuthenticationContext(token, request);
        filterChain.doFilter(request, response);
    }

    private boolean hasAuthorizationBearer(String headerAuthorization) {
        if (headerAuthorization == null || !headerAuthorization.startsWith("Bearer")) {
            return false;
        }
        return true;
    }

    private void setAuthenticationContext(String token, HttpServletRequest request) {
        String username = jwtService.getSubject(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
