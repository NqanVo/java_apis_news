package com.javaspring.blogapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "tbl_users")
@Table(name = "tbl_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
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


    public UserEntity(Long id, String createdBy, Date createdDate, String updatedBy, Date updatedDate, String username, String password, String fullName, String avatar, String phone, String address, Integer status, String verifyCodeEmail, boolean enabled, List<PostEntity> postEntityList, List<RoleEntity> roleEntities, List<CommentEntity> commentEntityList) {
        super(id, createdBy, createdDate, updatedBy, updatedDate);
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.avatar = avatar;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.verifyCodeEmail = verifyCodeEmail;
        this.enabled = enabled;
        this.postEntityList = postEntityList;
        this.roleEntities = roleEntities;
        this.commentEntityList = commentEntityList;
    }

    //UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleEntities.stream().map(role -> new SimpleGrantedAuthority(role.getCode())).collect(Collectors.toList());
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

}
