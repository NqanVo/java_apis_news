package com.javaspring.blogapi.config;

import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.model.RoleEntity;
import com.javaspring.blogapi.model.UserEntity;
import com.javaspring.blogapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserEntityByUsername(username);
        if (userEntity == null)
            throw new CustomException.NotFoundException("Not found user " + username);
        return userEntity;
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(List<RoleEntity> roleEntities) {
        return roleEntities.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
