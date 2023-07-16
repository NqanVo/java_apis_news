package com.javaspring.blogapi.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdatePasswordDTO {
    @NotNull(message = "Mật khẩu không được trống")
    @NotBlank(message = "Mật khẩu không được trống")
    @Size(min = 6, max = 30, message = "Tối thiểu là 6 ký tự và tối đa là 30")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Mật khẩu nên có ký tự thường, hoa và số")
    private String oldPassword;
    @NotNull(message = "Mật khẩu không được trống")
    @NotBlank(message = "Mật khẩu không được trống")
    @Size(min = 6, max = 30, message = "Tối thiểu là 6 ký tự và tối đa là 30")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Mật khẩu nên có ký tự thường, hoa và số")
    private String newPassword;
}
