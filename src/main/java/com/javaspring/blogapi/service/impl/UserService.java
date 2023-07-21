package com.javaspring.blogapi.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.javaspring.blogapi.config.jwt.EXPIRED_TYPE;
import com.javaspring.blogapi.config.jwt.JwtService2;
import com.javaspring.blogapi.config.oauth.ResponseUserInfoGitHubOAuth;
import com.javaspring.blogapi.config.oauth.ResponseUserInfoGoogleOAuth;
import com.javaspring.blogapi.converter.UserConverter;
import com.javaspring.blogapi.converter.UserInfoOAuthConverter;
import com.javaspring.blogapi.dto.auth.AuthLoginDTO;
import com.javaspring.blogapi.dto.auth.AuthResponseDTO;
import com.javaspring.blogapi.dto.user.UserUpdatePasswordDTO;
import com.javaspring.blogapi.dto.user.UserDTO;

import com.javaspring.blogapi.dto.user.UserUpdateDTO;
import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.model.RefreshTokenEntity;
import com.javaspring.blogapi.model.RoleEntity;
import com.javaspring.blogapi.model.UserEntity;
import com.javaspring.blogapi.repository.RefreshTokenRepository;
import com.javaspring.blogapi.repository.UserRepository;
import com.javaspring.blogapi.service.UserInterface;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private EmailService emailService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserInfoOAuthConverter oAuthConverter;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private Cloudinary cloudinary;

    //create new user or update user
    @Override
    public UserDTO save(UserDTO userDTO, TypesLogin type) {
        try {
            UserEntity newUserEntity;

            if (userRepository.findByUsername(userDTO.getUsername()) != null)
                throw new CustomException.BadRequestException("Người dùng đã tồn tại");

            RoleEntity roleEntityUser = roleService.findByName("ROLE_USER");
            String verifyCodeEmail = emailService.generateRandomString(50);
            newUserEntity = userConverter.UserDTOToUser(userDTO);
            newUserEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            newUserEntity.getRoleEntities().add(roleEntityUser);
            if (type.equals(TypesLogin.OAUTH)) {
                newUserEntity.setStatus(1);
                newUserEntity.setEnabled(true);
                newUserEntity.setVerifyCodeEmail("");
            } else {
                // * Chưa xác thực
                newUserEntity.setStatus(0);
                newUserEntity.setEnabled(false);
                newUserEntity.setVerifyCodeEmail(verifyCodeEmail);
            }

            newUserEntity = userRepository.save(newUserEntity);
            UserDTO dto = userConverter.UserToUserDTO(newUserEntity);
            // * Nếu là tài khoản thường, gửi email xác thực sau khi tạo tào khoản
            if (type.equals(TypesLogin.NORMAL)) emailService.sendMail(userDTO.getUsername(), verifyCodeEmail);
            return dto;
        } catch (MessagingException ex) {
            throw new CustomException.BadRequestException("Lỗi gửi Email xác thực, thử lại sau");
        }
    }

    @Override
    public void verifyCode(String verifyCodeEmail) {
        UserEntity userEntity = userRepository.findByVerifyCodeEmail(verifyCodeEmail);
        if (userEntity == null) throw new CustomException.BadRequestException("Mã xác thực không tồn tại");
        userEntity.setVerifyCodeEmail("");
        userEntity.setEnabled(true);
        userEntity.setStatus(1);
        userRepository.save(userEntity);
    }

//    @Override
//    @Transactional(rollbackOn = Exception.class)
//// quay về quá khứ nếu xãy ra lỗi trong csdl (bao gồm cả xóa ảnh vừa lưu/ không lưu db)
//    public UserDTO saveImage(String username, MultipartFile[] file) throws IOException {
//        UserEntity userEntity = userRepository.findByUsername(username);
//        if (userEntity == null) throw new CustomException.NotFoundException("Không tìm thấy người dùng: " + username);
//        if (!(filesService.isSingleFile(file) && filesService.notEmpty(file) && filesService.isImageFile(file[0]) && filesService.maxSize(file[0], 2))) {
//        }
//        // * 1 Lấy avatar cũ
//        String oldAvatar = userEntity.getAvatar();
//        // * 2 Lưu avatar mới
//        String newNameFile = new Date().getTime() + "_" + file[0].getOriginalFilename();
//        userEntity.setAvatar(newNameFile);
//        filesService.moveImageToFolder(file[0], newNameFile);
//        // * 3 Lưu vào csdl
//        userEntity = userRepository.save(userEntity);
//        // * 4 Xóa avatar cũ nếu có
//        if (!(oldAvatar == null || oldAvatar.equals(""))) {
//            filesService.deleteImageFromFolder(oldAvatar);
//        }
//        return userConverter.UserToUserDTO(userEntity);
//    }

    @Override
    @Transactional(rollbackOn = Exception.class)
// quay về quá khứ nếu xãy ra lỗi trong csdl (bao gồm cả xóa ảnh vừa lưu/ không lưu db)
    public UserDTO saveImage(String username, MultipartFile[] file) throws IOException {
        try {
            UserEntity userEntity = userRepository.findByUsername(username);
            if (userEntity == null)
                throw new CustomException.NotFoundException("Không tìm thấy người dùng: " + username);
            if (!(filesService.isSingleFile(file) && filesService.notEmpty(file) && filesService.isImageFile(file[0]) && filesService.maxSize(file[0], 2))) {
            }
            // * 1 Lấy avatar cũ
            String oldAvatar = userEntity.getAvatar();
            // * 2 Lưu avatar mới
            String urlImage = cloudinary.uploader().upload(file[0].getBytes(), ObjectUtils.emptyMap()).get("secure_url").toString();
            userEntity.setAvatar(urlImage);
            // * 3 Lưu vào csdl
            userEntity = userRepository.save(userEntity);
            // * 4 Xóa avatar cũ nếu có
            if (!(oldAvatar == null || oldAvatar.equals(""))) {
                String publicId = getPublicIdFromImageUrl(oldAvatar);
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
            return userConverter.UserToUserDTO(userEntity);
        } catch (Exception ex) {
            throw new CustomException.BadRequestException("Error uploading file" + ex.getMessage());
        }
    }

    @Override
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

    @Override
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

    @Override
    public UserDTO findByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) throw new CustomException.NotFoundException("Không tìm thấy người dùng " + username);
        return userConverter.UserToUserDTO(user);
    }

    @Override
    public void updateRoleUser(Long id, String[] roleNames) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        List<RoleEntity> roleEntityList = new ArrayList<>();
        for (String name : roleNames) {
            RoleEntity roleEntity = roleService.findByName(name);
            if (roleEntity != null)
                roleEntityList.add(roleEntity);
        }
        userEntity.setRoleEntities(roleEntityList);
        userRepository.save(userEntity);
    }

    @Override
    public List<UserDTO> findAll(Pageable pageable) {
        List<UserEntity> list = userRepository.findAll(pageable).getContent();
        List<UserDTO> listDTO = new ArrayList<>();
        for (UserEntity item : list)
            listDTO.add(userConverter.UserToUserDTO(item));
        return listDTO;
    }

    @Override
    public Long countItems() {
        return userRepository.count();
    }

    @Override
    public AuthResponseDTO loginUser(AuthLoginDTO authLoginDTO, HttpServletResponse response) {
        UserEntity userEntity = userRepository.findByUsername(authLoginDTO.getUsername());
        if (userEntity == null)
            throw new CustomException.BadRequestException("Sai tài khoản");
        if (!(passwordEncoder.matches(authLoginDTO.getPassword(), userEntity.getPassword())))
            throw new CustomException.BadRequestException("Sai mật khẩu");

        List<RefreshTokenEntity> refreshTokenOld = refreshTokenRepository.findRefreshTokenEntitiesByUserEntity(userEntity);
        if (refreshTokenOld.size() > 0) {
            refreshTokenOld.forEach(tokenOld -> refreshTokenRepository.deleteById(tokenOld.getId()));
        }
        String token = jwtService.generateAccessToken(userEntity, EXPIRED_TYPE.SHORT);
        RefreshTokenEntity refreshTokenNew = jwtService.generateRefreshToken(userEntity);

        refreshTokenRepository.save(refreshTokenNew);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authLoginDTO.getUsername(), authLoginDTO.getPassword()));
        response.setHeader("access_token", token);
        response.setHeader("refresh_token", refreshTokenNew.getRefreshToken());
        return new AuthResponseDTO(token, refreshTokenNew.getRefreshToken());
    }

    @Override
    public AuthResponseDTO loginOAuth(ResponseUserInfoGoogleOAuth googleUser) {
        UserEntity userEntity = userRepository.findByUsername(googleUser.getEmail());
        if (userEntity == null) {
            userEntity = oAuthConverter.OAuthGoogleEntity(googleUser);
            RoleEntity roleEntityUser = roleService.findByName("ROLE_USER");
            userEntity.setPassword(passwordEncoder.encode("123Abc"));
            userEntity.getRoleEntities().add(roleEntityUser);
            userEntity.setStatus(1);
            userEntity.setEnabled(true);
            userEntity.setVerifyCodeEmail("");
            userEntity = userRepository.save(userEntity);
        }
        String accessToken = jwtService.generateAccessToken(userEntity, EXPIRED_TYPE.SHORT);
        RefreshTokenEntity refreshTokenEntity = jwtService.generateRefreshToken(userEntity);
        refreshTokenRepository.save(refreshTokenEntity);
        return new AuthResponseDTO(accessToken, refreshTokenEntity.getRefreshToken());
    }

    @Override
    public AuthResponseDTO loginOAuth(ResponseUserInfoGitHubOAuth gitHubOAuth) {
        UserEntity userEntity = userRepository.findByUsername(gitHubOAuth.getLogin());
        if (userEntity == null) {
            userEntity = oAuthConverter.OAuthGitHubToEntity(gitHubOAuth);
            RoleEntity roleEntityUser = roleService.findByName("ROLE_USER");
            userEntity.setPassword(passwordEncoder.encode("123Abc"));
            userEntity.getRoleEntities().add(roleEntityUser);
            userEntity.setStatus(1);
            userEntity.setEnabled(true);
            userEntity.setVerifyCodeEmail("");
            userEntity = userRepository.save(userEntity);
        }
        String accessToken = jwtService.generateAccessToken(userEntity, EXPIRED_TYPE.SHORT);
        RefreshTokenEntity refreshTokenEntity = jwtService.generateRefreshToken(userEntity);
        refreshTokenRepository.save(refreshTokenEntity);
        return new AuthResponseDTO(accessToken, refreshTokenEntity.getRefreshToken());
//        return jwtService.generateAccessToken(userConverter.UserDTOToUser(userDTO), EXPIRED_TYPE.SHORT);
    }

    @Override
    public void logoutUser(String username) {
//        UserEntity userEntity = userRepository.findByUsername(username);
//        System.out.println(userEntity.getUsername());
//        if(userEntity != null) refreshTokenRepository.deleteRefreshTokenEntityByUserEntity(userEntity);
        refreshTokenRepository.deleteRefreshTokenEntityByUserEntityUsername(username);
    }

    @Override
    public AuthResponseDTO refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            final String headerAuthorization = request.getHeader("Authorization");
            if (headerAuthorization == null || !headerAuthorization.startsWith("Bearer"))
                throw new CustomException.UnauthorizedException("Cần có Refresh Token để yêu cầu");
            String refreshToken = headerAuthorization.substring(7);
            if (!jwtService.validateAccessToken(refreshToken)) throw new Exception();

            if (!refreshTokenRepository.existsRefreshTokenEntitiesByRefreshToken(refreshToken))
                throw new CustomException.UnauthorizedException("Refresh Token không tồn tại");
            String username = jwtService.getSubject(refreshToken);
            UserEntity userEntity = userRepository.findByUsername(username);
            if (userEntity == null) throw new Exception();
            String accessTokenNew = jwtService.generateAccessToken(userEntity, EXPIRED_TYPE.SHORT);
            RefreshTokenEntity refreshTokenNew = jwtService.generateRefreshToken(userEntity);
            // Xoa refreshToken cu va luu refreshToken moi
            refreshTokenRepository.deleteRefreshTokenEntityByUserEntityUsername(username);
            refreshTokenRepository.save(refreshTokenNew);
            response.setHeader("access_token", accessTokenNew);
            response.setHeader("refresh_token", refreshTokenNew.getRefreshToken());
            return new AuthResponseDTO(accessTokenNew, refreshToken);
        } catch (Exception ex) {
            throw new CustomException.UnauthorizedException("Phiên đăng nhập đã hết hạn");
        }
    }


    public ResponseFilter<UserDTO> findByFilters(Pageable pageable, String fullname, String phone, Boolean enabled, Date createFrom, Date createTo) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        CriteriaQuery<UserEntity> filterQuery = criteriaBuilder.createQuery(UserEntity.class);

        // Select count(*) from tbl_posts
        Root<UserEntity> countRoot = countQuery.from(UserEntity.class);
        countQuery.select(criteriaBuilder.count(countRoot));

        // Select * from tbl_posts
        Root<UserEntity> root = filterQuery.from(UserEntity.class);
        filterQuery.select(root);

        Predicate filterPredicate = buildPredicate(criteriaBuilder, root, fullname, phone, enabled, createFrom, createTo);
        Predicate countPredicate = buildPredicate(criteriaBuilder, countRoot, fullname, phone, enabled, createFrom, createTo);

        // * Truy vấn theo điều kiện, sắp xếp theo ngày tạo
        filterQuery.where(filterPredicate).orderBy(criteriaBuilder.asc(root.get("createdDate")));
        // * Truy vấn tổng kết quả
        countQuery.where(countPredicate);

        // * Phân trang
        TypedQuery<UserEntity> typedQuery = entityManager.createQuery(filterQuery);
        typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());
        // * Đếm
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery);

        // * Thực hiện truy vấn
        List<UserEntity> userEntities = typedQuery.getResultList();
        Long totalResults = countTypedQuery.getSingleResult();

        List<UserDTO> userDTOS = userEntities.stream().map(userEntity -> userConverter.UserToUserDTO(userEntity)).toList();

        return new ResponseFilter<UserDTO>(userDTOS, totalResults);
    }

    private Predicate buildPredicate(CriteriaBuilder criteriaBuilder, Root<UserEntity> root, String fullname, String phone, Boolean enabled, Date createFrom, Date createTo) {
        // Danh sách điều kiện nếu có
        List<Predicate> predicateList = new ArrayList<>();

        if (fullname != null && !fullname.equals("")) {
            Predicate fullnamePredicate = criteriaBuilder.like(root.get("fullName"), "%" + fullname + "%");
            predicateList.add(fullnamePredicate);
        }
        if (phone != null) {
            Predicate phonePredicate = criteriaBuilder.equal(root.get("phone"), phone);
            predicateList.add(phonePredicate);
        }
        if (enabled != null) {
            Predicate enabledPredicate = criteriaBuilder.equal(root.get("enabled"), enabled);
            predicateList.add(enabledPredicate);
        }

        if (createFrom != null && createTo != null) {
            Predicate createDatePredicate = criteriaBuilder.between(root.get("createdDate"), createFrom, createTo);
            predicateList.add(createDatePredicate);
        }
        // * Kết hợp các điều kiện
        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }

    private String getPublicIdFromImageUrl(String imageUrl) {
        int startIndex = imageUrl.lastIndexOf('/') + 1;
        int endIndex = imageUrl.lastIndexOf('.');
        return imageUrl.substring(startIndex, endIndex);
    }
}

