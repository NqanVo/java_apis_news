package com.javaspring.blogapi.config.jwt;

import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.model.RefreshTokenEntity;
import com.javaspring.blogapi.model.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Key;
import java.util.Date;

@Service
@Component
public class JwtService2 {
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    //    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24h
//    private static final long EXPIRE_DURATION_LONG = 24 * 60 * 60 * 1000; // 24h
    private static final long EXPIRE_DURATION_LONG = 24 * 60 * 60 * 1000; // 24h
    private static final long EXPIRE_DURATION = 10 * 60 * 1000; // 5p
    private static final Logger logger = LoggerFactory.getLogger(JwtService2.class.getName());

    public String generateAccessToken(UserEntity userEntity, EXPIRED_TYPE type) {
        long expired = EXPIRE_DURATION;
        if (type == EXPIRED_TYPE.LONG) expired = EXPIRE_DURATION_LONG;
        return Jwts.builder()
                .setSubject(userEntity.getUsername())
                .setIssuer("SERVER")
                .claim("roles", userEntity.getRoleEntities().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expired))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public RefreshTokenEntity generateRefreshToken(UserEntity userEntity) {
        String refreshToken = generateAccessToken(userEntity, EXPIRED_TYPE.LONG);
        return RefreshTokenEntity.builder()
                .userEntity(userEntity)
                .refreshToken(refreshToken)
                .expired(getExpired(refreshToken))
                .build();
    }

    public boolean validateAccessToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature -> Message: {} ", e.getMessage()); // Trả về thông báo lỗi cho chữ ký không hợp lệ
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token -> Message: {}", e.getMessage()); // Trả về thông báo lỗi cho token không hợp lệ
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token -> Message: {}", e.getMessage()); // Trả về thông báo lỗi cho token hết hạn
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token -> Message: {}", e.getMessage()); // Trả về thông báo lỗi cho token không được hỗ trợ
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty -> Message: {}", e.getMessage()); // Trả về thông báo lỗi khi chuỗi JWT claims rỗng
        }catch (Exception e){
            logger.error("Error {}", e.getMessage()); // Trả về thông báo lỗi khi chuỗi JWT claims rỗng
        }
        return false;
    }

    public Date getExpired(String token) {
        return parseClaims(token).getExpiration();
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
