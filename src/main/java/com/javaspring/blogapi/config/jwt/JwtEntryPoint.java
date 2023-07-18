package com.javaspring.blogapi.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javaspring.blogapi.dto.error.ErrorDTO;
import com.javaspring.blogapi.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        String errorMessage;
//        if (authException.getCause() instanceof ExpiredJwtException) {
//            errorMessage = "Expired JWT token";
//        } else if (authException.getCause() instanceof MalformedJwtException) {
//            errorMessage = "Invalid JWT token";
//        } else if (authException.getCause() instanceof SignatureException) {
//            errorMessage = "Invalid JWT signature";
//        } else if (authException.getCause() instanceof UnsupportedJwtException) {
//            errorMessage = "Unsupported JWT token";
//        } else if (authException.getCause() instanceof IllegalArgumentException) {
//            errorMessage = "JWT claims string is empty";
//        }
        if(authException.getCause() instanceof AccessDeniedException) System.out.println("Dung roi");
        ErrorDTO errorDTO = new ErrorDTO(LocalDateTime.now(), "Không có quyền truy cập cs", authException.getMessage());
        ResponseEntity<ErrorDTO> responseEntity = new ResponseEntity<>(errorDTO, HttpStatus.UNAUTHORIZED);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));

        String json = objectMapper.writeValueAsString(responseEntity.getBody());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
