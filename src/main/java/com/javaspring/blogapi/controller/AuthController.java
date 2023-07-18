package com.javaspring.blogapi.controller;

import com.javaspring.blogapi.config.jwt.EXPIRED_TYPE;
import com.javaspring.blogapi.config.jwt.JwtService2;
import com.javaspring.blogapi.config.oauth.ResponseTokenGitHubOAuth;
import com.javaspring.blogapi.config.oauth.ResponseTokenGoogleOAuth;
import com.javaspring.blogapi.config.oauth.ResponseUserInfoGitHubOAuth;
import com.javaspring.blogapi.config.oauth.ResponseUserInfoGoogleOAuth;
import com.javaspring.blogapi.dto.auth.AuthLoginDTO;
import com.javaspring.blogapi.dto.auth.AuthResponseDTO;
import com.javaspring.blogapi.dto.error.ErrorDTO;
import com.javaspring.blogapi.dto.user.UserDTO;
import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.model.UserEntity;
import com.javaspring.blogapi.repository.UserRepository;
import com.javaspring.blogapi.service.impl.EmailService;
import com.javaspring.blogapi.service.impl.TypesLogin;
import com.javaspring.blogapi.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


@RestController
@RequestMapping(path = "/auth")
@Tag(name = "Authentication Controller")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Value("${google.client.id}")
    private String googleClientId;
    @Value("${google.client.secret}")
    private String googleClientSecret;
    @Value("${google.redirect.uri}")
    private String googleRedirectUri;

    @Value("${github.client.id}")
    private String githubClientId;
    @Value("${github.client.secret}")
    private String githubClientSecret;
    @Value("${github.redirect.uri}")
    private String githubRedirectUri;

    @Value("${domain.name.frontend}")
    private String urlFrontEnd;

    @Operation(
            description = "Đăng nhập bằng username/password",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuthLoginDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "400", description = "Thông tin nhập không hợp lệ", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthLoginDTO authLoginDTO, HttpServletResponse response) {
        return ResponseEntity.ok().body(new AuthResponseDTO(userService.loginUser(authLoginDTO, response)));
    }

    @Operation(
            description = "Đăng xuất",
            responses = {
                    @ApiResponse(content = @Content(), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @GetMapping(path = "/logout/{username}")
    @PreAuthorize("hasAnyRole('USER','ADMIN') and #username == authentication.name")
    public void logout(@PathVariable String username) {
        userService.logoutUser(username);
    }

    @Operation(
            description = "refresh token với header Bearer token",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuthResponseDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Phiên đăng nhập đã hết hạn", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @GetMapping(path = "/refresh-token")
    public ResponseEntity<AuthResponseDTO> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok().body(userService.refreshToken(request, response));
    }

    @Operation(
            description = "Đăng ký, cần xác thực email để kích hoạt tài khoản",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "400", description = "Thông tin nhập không hợp lệ", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            })
    @PostMapping(path = "/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        UserDTO newUser = userService.save(userDTO, TypesLogin.NORMAL);
        URI uri = URI.create("/users/" + newUser.getId());
        return ResponseEntity.created(uri).body(newUser);
    }

    @Operation(
            description = "Đăng nhập bằng OAuth Google",
            responses = {
                    @ApiResponse(content = @Content(), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Email chưa được kích hoạt", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            })
    @GetMapping(path = "/oauth/google")
    public ResponseEntity<?> handleGoogleOAuth(@RequestParam("code") String code) {
        // * 1 Nhận code từ Google API chuyển hướng về, tạo body để yêu cầu lấy token từ Google API
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("redirect_uri", googleRedirectUri);
        body.add("grant_type", "authorization_code");

        // * Gửi yêu cầu lấy token Google OAuth
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<ResponseTokenGoogleOAuth> response = restTemplate().postForEntity(
                "https://oauth2.googleapis.com/token",
                request,
                ResponseTokenGoogleOAuth.class
        );
        // * Nhận về response token Google OAuth
        ResponseTokenGoogleOAuth responseTokenGoogleOAuth = response.getBody();

        if (responseTokenGoogleOAuth != null) {
            String idToken = responseTokenGoogleOAuth.getId_token();
            String accessToken = responseTokenGoogleOAuth.getAccess_token();

            // * 2 Gửi id_token và access_token để lấy thông tin người dùng từ Google
            HttpHeaders userInfoHeaders = new HttpHeaders();
            userInfoHeaders.setBearerAuth(idToken);
            HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);
            ResponseEntity<ResponseUserInfoGoogleOAuth> userInfoResponse = restTemplate().exchange(
                    "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token={accessToken}",
                    HttpMethod.GET,
                    userInfoRequest,
                    ResponseUserInfoGoogleOAuth.class,
                    accessToken
            );
            // * Nhận về response user info Google OAuth
            ResponseUserInfoGoogleOAuth userInfoGoogle = userInfoResponse.getBody();
            if (userInfoGoogle != null && userInfoGoogle.isVerified_email()) {
                // * 3 Redirect người dùng về trang login với parameter access_token
                String access_token = userService.loginOAuth(userInfoGoogle);
                String redirectUrl = urlFrontEnd + "/login/oauth?access_token=" + access_token;
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, redirectUrl)
                        .build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Google email not verified");
    }

    @Operation(
            description = "Đăng nhập bằng OAuth GitHub",
            responses = {
                    @ApiResponse(content = @Content(), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Email chưa được kích hoạt", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            })
    @GetMapping(path = "/oauth/github")
    public ResponseEntity<?> handleGitHubOAuth(@RequestParam("code") String code) {
        // * 1 Nhận code từ GitHub Author chuyển hướng về, tạo body để yêu cầu lấy token từ GitHub
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", githubClientId);
        body.add("client_secret", githubClientSecret);
        body.add("redirect_uri", githubRedirectUri);
        // * Gửi yêu cầu lấy token GitHub OAuth
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<ResponseTokenGitHubOAuth> response = restTemplate().postForEntity(
                "https://github.com/login/oauth/access_token",
                request,
                ResponseTokenGitHubOAuth.class
        );
        // * Nhận về response token GitHub OAuth
        ResponseTokenGitHubOAuth responseTokenGitHubOAuth = response.getBody();

        if (responseTokenGitHubOAuth != null) {
            String accessToken = responseTokenGitHubOAuth.getAccess_token();

            // * 2 Gửi id_token và access_token để lấy thông tin người dùng từ Google
            HttpHeaders userInfoHeaders = new HttpHeaders();
            userInfoHeaders.setBearerAuth(accessToken);
            HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);
            ResponseEntity<ResponseUserInfoGitHubOAuth> userInfoResponse = restTemplate().exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    userInfoRequest,
                    ResponseUserInfoGitHubOAuth.class
            );
            // * Nhận về response user info Google OAuth
            ResponseUserInfoGitHubOAuth userInfoGitHub = userInfoResponse.getBody();
            if (userInfoGitHub != null) {
                // * 3 Redirect người dùng về trang login với parameter access_token
                String access_token = userService.loginOAuth(userInfoGitHub);
                String redirectUrl = urlFrontEnd + "/login/oauth?access_token=" + access_token;
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, redirectUrl)
                        .build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Google email not verified");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


//    @GetMapping(path = "/verify-email/{email}")
//    public void sendCodeVerifyEmail(@PathVariable String email) throws MessagingException {
//        emailService.sendMail(email,"123");
//    }

    @Operation(
            description = "Verify email",
            responses = {
                    @ApiResponse(content = @Content(), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "400", description = "Code không chính xác", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            })
    @GetMapping(path = "/verify-email")
    public ResponseEntity<Object> verifyEmail(@RequestParam String verify_code) {
        userService.verifyCode(verify_code);
        String redirectUrl = "https://nqanvo.github.io/verifysuccess/";
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }
}

