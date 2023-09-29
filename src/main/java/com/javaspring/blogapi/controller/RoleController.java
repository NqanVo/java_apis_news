package com.javaspring.blogapi.controller;

import com.javaspring.blogapi.dto.error.ErrorDTO;
import com.javaspring.blogapi.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users/roles")
@Tag(name = "Roles Controller")
@SecurityRequirement(name = "Authorization")
public class RoleController {
    @Autowired
    UserService userService;
    @Operation(
            description = "Cập nhật quyền cho user, quyền ADMIN",
            responses = {
                    @ApiResponse(content = @Content(mediaType = "Cập nhật quyền thành công"), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> updateRoleUser(@PathVariable Long id, @RequestBody String[] roleNames) {
        if (roleNames.length == 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quyền không được trống");
        userService.updateRoleUser(id, roleNames);
        return ResponseEntity.ok().body("Cập nhật quyền thành công");
    }
}
