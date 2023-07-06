package com.javaspring.blogapi.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.javaspring.blogapi.dto.BaseDTO;
import com.javaspring.blogapi.model.RoleEntity;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getNameRoles() {
        return nameRoles;
    }

    public void setNameRoles(List<String> nameRoles) {
        this.nameRoles = nameRoles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

//    public Integer getStatus() {
//        return status;
//    }
//
//    public void setStatus(Integer status) {
//        this.status = status;
//    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
