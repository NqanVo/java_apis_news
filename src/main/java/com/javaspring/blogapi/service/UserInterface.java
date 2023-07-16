package com.javaspring.blogapi.service;

import com.javaspring.blogapi.config.oauth.ResponseUserInfoGitHubOAuth;
import com.javaspring.blogapi.config.oauth.ResponseUserInfoGoogleOAuth;
import com.javaspring.blogapi.dto.auth.AuthLoginDTO;
import com.javaspring.blogapi.dto.user.UserDTO;
import com.javaspring.blogapi.dto.user.UserUpdateDTO;
import com.javaspring.blogapi.dto.user.UserUpdatePasswordDTO;
import com.javaspring.blogapi.service.impl.TypesLogin;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserInterface   {
    UserDTO save(UserDTO userDTO, TypesLogin type) throws MessagingException;
    @Transactional(rollbackOn = Exception.class)
    UserDTO saveImage(String username, MultipartFile[] file) throws IOException;
    UserDTO update(UserUpdateDTO userUpdateDTO, String username);
    void updatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO, String username);
    UserDTO findByUsername(String username);
    void updateRoleUser(Long id, String[] roleNames);
    List<UserDTO> findAll(Pageable pageable);
    Long countItems();
    String loginUser(AuthLoginDTO authLoginDTO);
    String loginOAuth(ResponseUserInfoGoogleOAuth googleUser) throws MessagingException;
    String loginOAuth(ResponseUserInfoGitHubOAuth gitHubOAuth) throws MessagingException;
}
