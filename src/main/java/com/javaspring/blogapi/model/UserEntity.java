package com.javaspring.blogapi.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "tbl_users")
@Table(name = "tbl_users")
public class UserEntity extends BaseModel implements UserDetails {
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String fullName;
    @Column
    private String avatar;
    @Column
    private String phone;
    @Column
    private String address;
    @Column
    private Integer status;
    @Column
    private String verifyCodeEmail;
    @Column
    private boolean enabled;

    @OneToMany(mappedBy = "userEntity",cascade = CascadeType.ALL)
    private List<PostEntity> postEntityList = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tbl_user_role", //ten bảng trung giang
            joinColumns = @JoinColumn(name = "id_user"), //khoa ngoại user
            inverseJoinColumns = @JoinColumn(name = "id_role") //khoa ngoại role
    )
    private List<RoleEntity> roleEntities = new ArrayList<>();

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<CommentEntity> commentEntityList = new ArrayList<>();

    public void setUsername(String username) {
        this.username = username;
    }

    //UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleEntities.stream().map(role -> new SimpleGrantedAuthority(role.getCode())).collect(Collectors.toList());
    }

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

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<RoleEntity> getRoles() {
        return roleEntities;
    }

    public void setRoles(List<RoleEntity> roleEntities) {
        this.roleEntities = roleEntities;
    }

    public String getVerifyCodeEmail() {
        return verifyCodeEmail;
    }

    public void setVerifyCodeEmail(String verifyCodeEmail) {
        this.verifyCodeEmail = verifyCodeEmail;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
