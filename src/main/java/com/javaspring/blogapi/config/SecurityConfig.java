package com.javaspring.blogapi.config;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.javaspring.blogapi.config.jwt.JwtAuthFilter2;
import com.javaspring.blogapi.config.jwt.JwtEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    JwtAuthFilter2 jwtAuthFilter;
    @Autowired
    JwtEntryPoint jwtEntryPoint;

    @Value("${cloudinary.cloud_name}")
    private String cloudName;
    @Value("${cloudinary.api_key}")
    private String apiKey;
    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);
        http.sessionManagement(ss -> ss.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(handle -> handle.authenticationEntryPoint(jwtEntryPoint));
        http.authorizeHttpRequests(auth ->
                auth
                        // * SWAGGER
                        .requestMatchers("/api/v1/auth/**",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html#/").permitAll()
                        // * IMAGE
                        .requestMatchers("/images/**").permitAll()
                        // * AUTH
                        .requestMatchers("/auth/**").permitAll()
                        // * USER
                        .requestMatchers(HttpMethod.GET, "/users", "/users/**").permitAll()
                        // * POST
                        .requestMatchers(HttpMethod.GET, "/posts", "/posts/**").permitAll()
                        // * CATEGORY
                        .requestMatchers(HttpMethod.GET, "/category", "/category/**").permitAll()
                        .anyRequest().authenticated());
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    //Cau hinh CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Lay thoi gian tao + cap nhat
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SecurityConfig.AuditorAwareImpl();
    }

    public static class AuditorAwareImpl implements AuditorAware<String> {
        @Override
        public Optional<String> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) return Optional.empty();

            Object userDetails = authentication.getPrincipal();
            if (userDetails instanceof UserDetails) {
                return Optional.of(((UserDetails) userDetails).getUsername());
            } else if (userDetails instanceof String) {
                return Optional.of((String) userDetails);
            }
            return Optional.empty();
        }
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }
}
