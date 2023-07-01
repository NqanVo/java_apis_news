package com.javaspring.blogapi.converter;

import com.javaspring.blogapi.config.oauth.ResponseUserInfoGitHubOAuth;
import com.javaspring.blogapi.config.oauth.ResponseUserInfoGoogleOAuth;
import com.javaspring.blogapi.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserInfoOAuthConverter {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserEntity OAuthGitHubToEntity(ResponseUserInfoGitHubOAuth userInfoGitHubOAuth){
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userInfoGitHubOAuth.getLogin());
        userEntity.setFullName(userInfoGitHubOAuth.getName());
        userEntity.setStatus(1);
        userEntity.setPassword(passwordEncoder.encode(userInfoGitHubOAuth.getLogin()));
        return userEntity;
    }

    public UserEntity OAuthGitGoogleEntity(ResponseUserInfoGoogleOAuth userInfoGoogleOAuth){
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userInfoGoogleOAuth.getEmail());
        userEntity.setFullName(userInfoGoogleOAuth.getName());
        userEntity.setStatus(1);
        userEntity.setPassword(passwordEncoder.encode(userInfoGoogleOAuth.getEmail()));
        return userEntity;
    }
}
