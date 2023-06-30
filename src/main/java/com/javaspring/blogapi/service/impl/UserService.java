package com.javaspring.blogapi.service.impl;

import com.javaspring.blogapi.config.jwt.JwtService2;
import com.javaspring.blogapi.converter.UserConverter;
import com.javaspring.blogapi.dto.auth.AuthLoginDTO;
import com.javaspring.blogapi.dto.user.UserUpdatePasswordDTO;
import com.javaspring.blogapi.dto.user.UserDTO;

import com.javaspring.blogapi.dto.user.UserUpdateDTO;
import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.model.RoleEntity;
import com.javaspring.blogapi.model.UserEntity;
import com.javaspring.blogapi.repository.UserRepository;
import com.javaspring.blogapi.service.UserInterface;
import com.javaspring.blogapi.service.FilesService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService implements UserInterface {
    @Autowired
    private JwtService2 jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private FilesService filesService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //create new user or update user
    @Override
    public UserDTO save(UserDTO userDTO) {
        UserEntity newUserEntity;

        if (userRepository.findByUsername(userDTO.getUsername()) != null)
            throw new CustomException.BadRequestException("Người dùng đã tồn tại");

        RoleEntity roleEntityUser = roleService.findByName("ROLE_USER");
        newUserEntity = userConverter.UserDTOToUser(userDTO);
        newUserEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUserEntity.getRoles().add(roleEntityUser);
        newUserEntity.setStatus(1);

        newUserEntity = userRepository.save(newUserEntity);
        UserDTO dto = userConverter.UserToUserDTO(newUserEntity);
        return dto;
    }

    @Transactional(rollbackOn = Exception.class)// quay về quá khứ nếu xãy ra lỗi trong csdl (bao gồm cả xóa ảnh vừa lưu/ không lưu db)
    public UserDTO saveImage(String username, MultipartFile[] file) throws IOException {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) throw new CustomException.NotFoundException("Không tìm thấy người dùng: " + username);
        if (!(filesService.isSingleFile(file) && filesService.notEmpty(file) && filesService.isImageFile(file[0]) && filesService.maxSize(file[0], 2))) {
        }
        // * 1 Lấy avatar cũ
        String oldAvatar = userEntity.getAvatar();
        // * 2 Lưu avatar mới
        String newNameFile = new Date().getTime() + "_" + file[0].getOriginalFilename();
        userEntity.setAvatar(newNameFile);
        filesService.moveImageToFolder(file[0], newNameFile);
        // * 3 Lưu vào csdl
        userEntity = userRepository.save(userEntity);
        // * 4 Xóa avatar cũ nếu có
        if (!(oldAvatar == null || oldAvatar.equals(""))) {
            filesService.deleteImageFromFolder(oldAvatar);
        }
        return userConverter.UserToUserDTO(userEntity);
    }

    public UserDTO update(UserUpdateDTO userUpdateDTO, String username) {
        UserEntity userEntity;
        UserEntity oldUserEntity = userRepository.findByUsername(username);
        if (oldUserEntity == null)
            throw new CustomException.NotFoundException("Không tìm thấy người dùng " + username);

        userEntity = userConverter.UpdateInfo_UserDTOToUser(userUpdateDTO, oldUserEntity);

        userEntity = userRepository.save(userEntity);
        UserDTO dto = userConverter.UserToUserDTO(userEntity);
        return dto;
    }

    public void updatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO, String username) {
        UserEntity oldUserEntity = userRepository.findByUsername(username);

        if (oldUserEntity == null)
            throw new CustomException.NotFoundException("Không tìm thấy người dùng " + username);
        if (!passwordEncoder.matches(userUpdatePasswordDTO.getOldPassword(), oldUserEntity.getPassword()))
            throw new CustomException.BadRequestException("Mật khẩu cũ không chính xác");

        oldUserEntity.setPassword(passwordEncoder.encode(userUpdatePasswordDTO.getNewPassword()));
        userRepository.save(oldUserEntity);

        UserDTO dto = userConverter.UserToUserDTO(oldUserEntity);
    }

    public UserDTO findByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) throw new CustomException.NotFoundException("Không tìm thấy người dùng " + username);
        return userConverter.UserToUserDTO(user);
    }

    public void updateRoleUser(Long id, String[] roleNames) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        List<RoleEntity> roleEntityList = new ArrayList<>();
        for (String name : roleNames) {
            RoleEntity roleEntity = roleService.findByName(name);
            if (roleEntity != null)
                roleEntityList.add(roleEntity);
        }
        userEntity.setRoles(roleEntityList);
        userRepository.save(userEntity);
    }

    public List<UserDTO> findAll(Pageable pageable) {
        List<UserEntity> list = userRepository.findAll(pageable).getContent();
        List<UserDTO> listDTO = new ArrayList<>();
        for (UserEntity item : list)
            listDTO.add(userConverter.UserToUserDTO(item));
        return listDTO;
    }

    public Long countItems() {
        return userRepository.count();
    }

    public String loginUser(AuthLoginDTO authLoginDTO) {
        UserEntity userEntity = userRepository.findByUsername(authLoginDTO.getUsername());
        if (userEntity == null)
            throw new CustomException.BadRequestException("Sai tài khoản");
        if (!(passwordEncoder.matches(authLoginDTO.getPassword(), userEntity.getPassword())))
            throw new CustomException.BadRequestException("Sai mật khẩu");

        String token = jwtService.generateAccessToken(userEntity);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authLoginDTO.getUsername(), authLoginDTO.getPassword()));
        return token;
    }
}
