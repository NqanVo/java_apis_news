package com.javaspring.blogapi.dto.auth;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthLoginDTO {
    @NotNull(message = "Tài khoản không được trống")
    @NotBlank(message = "Tài khoản không được có khoảng trắng ở đầu/cuối")
    @Size(min = 5, max = 50, message = "Tối thiểu là 5 ký tự và tối đa là 50")
//    @Pattern(regexp = "(?=^[^\\s].*)(?=.*[^\\s]$).*", message = "Không được để thừa dấu cách đầu/cuối đoạn")
    @Email(message = "username phải là Email hợp lệ")
    private String username;
    @NotNull(message = "Mật khẩu không được trống")
    @NotBlank(message = "Mật khẩu không được có khoảng trắng ở đầu/cuối")
    @Size(min = 6, max = 30, message = "Tối thiểu là 6 ký tự và tối đa là 30")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Mật khẩu nên có ký tự thường, hoa và số")
    private String password;
}
