package com.javaspring.blogapi.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.javaspring.blogapi.dto.BaseDTO;
import com.javaspring.blogapi.model.RoleEntity;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends BaseDTO {
    @NotNull(message = "Tài khoản không được trống")
    @NotBlank(message = "Mật khẩu không được trống")
    @Size(min = 5, max = 50, message = "Tối thiểu là 5 ký tự và tối đa là 50")
//    @Pattern(regexp = "^[a-z0-9]+$", message = "Tài khoản người dùng chỉ có thể là ký tự thường và số")
    @Email(message = "username phải là Email hợp lệ")
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Mật khẩu không được trống")
    @NotBlank(message = "Mật khẩu không được trống")
    @Size(min = 6, max = 30, message = "Tối thiểu là 6 ký tự và tối đa là 30")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Mật khẩu nên có ký tự thường, hoa và số")
    private String password;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String avatar;
    //    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//    private Integer status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> nameRoles = new ArrayList<>();

    public UserDTO(Long id, String createdBy, Date createdDate, String updatedBy, Date updatedDate, @NotNull(message = "Tài khoản không được trống") String username, @NotNull(message = "Mật khẩu không được trống") String password, @NotNull(message = "Tên không được trống") String fullName, @NotNull(message = "Số điện thoại không được trống") String phone, @NotNull(message = "Địa chỉ không được trống") String address, String avatar, boolean enabled, List<String> nameRoles) {
        super(id, createdBy, createdDate, updatedBy, updatedDate);
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.avatar = avatar;
        this.enabled = enabled;
        this.nameRoles = nameRoles;
    }
}
