package com.javaspring.blogapi.config;

import com.javaspring.blogapi.model.RoleEntity;
import com.javaspring.blogapi.model.UserEntity;
import com.javaspring.blogapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IsUserOrIsAdmin {
    @Autowired UserRepository userRepository;
    private UserEntity userEntity;
    private boolean isAdmin;

    public IsUserOrIsAdmin(UserEntity userEntity, boolean isAdmin) {
        this.userEntity = userEntity;
        this.isAdmin = isAdmin;
    }

    public IsUserOrIsAdmin() {
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    private void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    private void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public IsUserOrIsAdmin getUserAndIsAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object userDetails = authentication.getPrincipal();
        // * Lấy username từ jwt
        String username = ((UserDetails) userDetails).getUsername();
        // * Kiểm tra xem phải admin không
        setUserEntity(userRepository.findByUsername(username));
        List<String> roles = new ArrayList<>();
        for (RoleEntity role : userEntity.getRoles())
            roles.add(role.getCode());
        setAdmin(roles.contains("ROLE_ADMIN"));

        return new IsUserOrIsAdmin(userEntity, isAdmin);
    }
}
