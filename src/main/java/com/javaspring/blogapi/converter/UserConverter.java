package com.javaspring.blogapi.converter;

import com.javaspring.blogapi.dto.user.UserUpdatePasswordDTO;
import com.javaspring.blogapi.dto.user.UserDTO;
import com.javaspring.blogapi.dto.user.UserUpdateDTO;
import com.javaspring.blogapi.model.RoleEntity;
import com.javaspring.blogapi.model.UserEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class UserConverter {
    public UserEntity UserDTOToUser(UserDTO userDTO){
        UserEntity userEntity = new UserEntity();

        BeanUtils.copyProperties(userDTO, userEntity);
        return userEntity;
    }

    public UserDTO UserToUserDTO(UserEntity userEntity){
        UserDTO userDTO = new UserDTO();

        BeanUtils.copyProperties(userEntity, userDTO);
        List<String> nameRoles = new ArrayList<>();
        for (RoleEntity role : userEntity.getRoles())
            nameRoles.add(role.getName());
        userDTO.setNameRoles(nameRoles);
        return userDTO;
    }

    public UserEntity UpdateInfo_UserDTOToUser(UserUpdateDTO userUpdateDTO, UserEntity userEntity){
        String createBy = userEntity.getCreatedBy();
        Date createDate = userEntity.getCreatedDate();

        BeanUtils.copyProperties(userUpdateDTO, userEntity);
        userEntity.setCreatedBy(createBy);
        userEntity.setCreatedDate(createDate);

        return userEntity;
    }
}
