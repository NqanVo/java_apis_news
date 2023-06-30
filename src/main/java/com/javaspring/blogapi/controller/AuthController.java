package com.javaspring.blogapi.controller;

import com.javaspring.blogapi.dto.CategoryDTO;
import com.javaspring.blogapi.dto.auth.AuthLoginDTO;
import com.javaspring.blogapi.dto.auth.AuthResponseDTO;
import com.javaspring.blogapi.dto.error.ErrorDTO;
import com.javaspring.blogapi.dto.user.UserDTO;
import com.javaspring.blogapi.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(path = "/auth")
@Tag(name = "Authentication Controller")
public class AuthController {
    @Autowired
    private UserService userService;

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
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthLoginDTO authLoginDTO) {
        return ResponseEntity.ok().body(new AuthResponseDTO(userService.loginUser(authLoginDTO)));
    }
    @Operation(
            description = "Đăng ký",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "400", description = "Thông tin nhập không hợp lệ", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            })
    @PostMapping(path = "/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        UserDTO newUser = userService.save(userDTO);
        URI uri = URI.create("/users/" + newUser.getId());
        return ResponseEntity.created(uri).body(newUser);
    }

    //    @GetMapping(path = "/logout")
//    public void logout() {
//        return;
//    }

}
