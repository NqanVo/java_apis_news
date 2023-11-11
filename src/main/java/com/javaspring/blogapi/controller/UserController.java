package com.javaspring.blogapi.controller;

import com.javaspring.blogapi.dto.error.ErrorDTO;
import com.javaspring.blogapi.dto.user.UserUpdatePasswordDTO;
import com.javaspring.blogapi.dto.user.UserDTO;
import com.javaspring.blogapi.dto.user.UserUpdateDTO;
import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.response.ResponseList;
import com.javaspring.blogapi.service.impl.FilesService;
import com.javaspring.blogapi.service.impl.ResponseFilter;
import com.javaspring.blogapi.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping(path = "/users")
@Tag(name = "Users Controller")
@SecurityRequirement(name = "Authorization")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    FilesService filesService;

    @GetMapping
    @Operation(
            description = "Xem danh sách người dùng",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    public ResponseList<UserDTO> findByFilter(
            @RequestParam(required = false) Long currentPage,
            @RequestParam(required = false) Long limit,
            @RequestParam(required = false) String fullname,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTo) {
        ResponseList<UserDTO> res = new ResponseList<>();
        Long actualLimit = (limit != null && limit > 0) ? limit : 10;
        Long actualCurrentPage = (currentPage != null && currentPage > 0) ? currentPage : 1;
        Pageable pageable = PageRequest.of(actualCurrentPage.intValue() - 1, actualLimit.intValue());

        if (createFrom != null && createTo == null)
            throw new CustomException.BadRequestException("Khoảng ngày thiếu dữ kiện đích");
        //Nếu ngày từ = ngày đến thì ngày đến + 1 ngày
        if (createFrom != null && (createFrom == createTo)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createTo);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            createTo = calendar.getTime();
        }

        ResponseFilter<UserDTO> result = userService.findByFilters(pageable, fullname, phone, enabled, createFrom, createTo);

        return new ResponseList<UserDTO>(
                actualCurrentPage,
                actualLimit,
                (long) Math.ceil((double) result.total() / actualLimit),
                result.total(),
                result.data());
    }

    @Operation(
            description = "Xem thông tin người dùng bằng username",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        return ResponseEntity.ok().body(userService.findByUsername(username));
    }

    @Operation(
            description = "Sửa thông tin người dùng, admin cũng có quyền chỉnh sửa",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PutMapping("/{username}")
    @PreAuthorize("hasAnyRole('USER','ADMIN') and #username == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String username, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok().body(userService.update(userUpdateDTO, username));
    }

    @Operation(
            description = "Sửa mật khẩu người dùng, admin cũng có quyền chỉnh sửa",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PutMapping("/{username}/password")
    @PreAuthorize("hasAnyRole('USER','ADMIN') and #username == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<String> updatePasswordUser(@PathVariable String username, @Valid @RequestBody UserUpdatePasswordDTO userUpdatePasswordDTO) {
        userService.updatePassword(userUpdatePasswordDTO, username);
        return ResponseEntity.ok().body(String.format("Cập nhật mật khẩu %s thành công", username));
    }

    @Operation(
            description = "Tải lên avatar người dùng, admin cũng có quyền chỉnh sửa",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PutMapping("/{username}/upload-image")
    @PreAuthorize("hasAnyRole('USER','ADMIN') and #username == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<UserDTO> upload(@PathVariable String username, @RequestPart MultipartFile[] file) throws IOException {
        return ResponseEntity.ok().body(userService.saveImage(username, file));
    }
}
