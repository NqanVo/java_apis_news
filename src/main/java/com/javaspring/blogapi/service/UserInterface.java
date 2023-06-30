package com.javaspring.blogapi.service;

import com.javaspring.blogapi.dto.user.UserDTO;

// extends UserDetailsService
public interface UserInterface   {
    UserDTO save(UserDTO userDTO);

}
