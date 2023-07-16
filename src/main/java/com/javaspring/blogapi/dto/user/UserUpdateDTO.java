package com.javaspring.blogapi.dto.user;

import com.javaspring.blogapi.dto.BaseDTO;
import jakarta.persistence.Column;
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
public class UserUpdateDTO extends BaseDTO {
    @NotNull(message = "Tên không được trống")
    @NotBlank(message = "Mật khẩu không được trống")
    @Size(min = 5, max = 50, message = "Tối thiểu là 5 ký tự và tối đa là 50")
    @Pattern(regexp = "(?=^[^\\s].*)(?=.*[^\\s]$).*", message = "Không được để thừa dấu cách đầu/cuối đoạn")
    private String fullName;

    @NotNull(message = "Số điện thoại không được trống")
    @NotBlank(message = "Số điện thoại không được trống")
    @Size(min = 7, max = 15, message = "Tối thiểu là 7 ký tự và tối đa là 15")
    @Pattern(regexp = "\\d+", message = "Chỉ được nhập số")
    private String phone;
    @NotNull(message = "Địa chỉ không được trống")
    @NotBlank(message = "Địa chỉ không được trống")
    @Size(min = 5, max = 50, message = "Tối thiểu là 5 ký tự và tối đa là 100")
    @Pattern(regexp = "(?=^[^\\s].*)(?=.*[^\\s]$).*", message = "Không được để thừa dấu cách đầu/cuối đoạn")
    @Column
    private String address;
}
