package com.javaspring.blogapi.userTest;

import com.javaspring.blogapi.converter.UserConverter;
import com.javaspring.blogapi.dto.user.UserDTO;
import com.javaspring.blogapi.dto.user.UserUpdateDTO;
import com.javaspring.blogapi.dto.user.UserUpdatePasswordDTO;
import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.model.CommentEntity;
import com.javaspring.blogapi.model.PostEntity;
import com.javaspring.blogapi.model.RoleEntity;
import com.javaspring.blogapi.model.UserEntity;
import com.javaspring.blogapi.repository.RoleRepository;
import com.javaspring.blogapi.repository.UserRepository;
import com.javaspring.blogapi.service.impl.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserConverter userConverter;
    @Mock
    private EmailService emailService;
    @Mock
    private EntityManager entityManager;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;
    @Mock
    private CriteriaBuilder criteriaBuilder;

    @InjectMocks
    private UserService userService;

    RoleEntity roleUserEntity = RoleEntity.builder()
            .code("ROLE_USER")
            .name("ROLE_USER")
            .build();
    RoleEntity roleAdminEntity = RoleEntity.builder()
            .code("ROLE_ADMIN")
            .name("ROLE_ADMIN")
            .build();

    private List<UserEntity> userEntityList;
    private List<UserDTO> userDTOList;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        UserDTO userDTO1 = new UserDTO(
                1L,
                "admin",
                new Date(),
                "admin",
                new Date(),
                "nganvo@gmail.com",
                "$2a$12$AsxK83vaTtGihFMr9jyziOkJ7mJNclvszlKsa/A4L8lPT1W5BzwOi",
                "phuc ngan",
                "12345678",
                "BenTre",
                null,
                true,
                new ArrayList<>(Arrays.asList("ROLE_USER", "ROLE_ADMIN")));
        UserDTO userDTO2 = new UserDTO(
                2L,
                "admin",
                new Date(),
                "admin",
                new Date(),
                "nganvo2@gmail.com",
                "123Abc",
                "phuc ngan 2",
                "12345678",
                "BenTre",
                null,
                true,
                new ArrayList<>(Arrays.asList("ROLE_USER")));
        UserDTO userDTO3 = new UserDTO(
                3L,
                "admin",
                new Date(),
                "admin",
                new Date(),
                "nganvo3@gmail.com",
                "123Abc",
                "phuc ngan 3",
                "12345678",
                "BenTre",
                null,
                true,
                new ArrayList<>(Arrays.asList("ROLE_USER")));

        UserEntity userEntity1 = new UserEntity(
                1L,
                "admin",
                new Date(),
                "admin",
                new Date(),
                "nganvo@gmail.com",
                "$2a$12$AsxK83vaTtGihFMr9jyziOkJ7mJNclvszlKsa/A4L8lPT1W5BzwOi",
                "phuc ngan",
                null,
                "12345678",
                "BenTre",
                1,
                null,
                true,
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList(roleAdminEntity, roleUserEntity)), new ArrayList<>());
        UserEntity userEntity2 = new UserEntity(
                2L,
                "admin",
                new Date(),
                "admin",
                new Date(),
                "nganvo2@gmail.com",
                "123Abc",
                "phuc ngan 2",
                null,
                "12345678",
                "BenTre",
                1,
                null,
                true,
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList(roleAdminEntity, roleUserEntity)), new ArrayList<>());
        UserEntity userEntity3 = new UserEntity(
                3L,
                "admin",
                new Date(),
                "admin",
                new Date(),
                "nganvo3@gmail.com",
                "123Abc",
                "phuc ngan 3",
                null,
                "12345678",
                "BenTre",
                1,
                null,
                true,
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList(roleAdminEntity, roleUserEntity)), new ArrayList<>());

        userDTOList = new ArrayList<>(Arrays.asList(userDTO1, userDTO2, userDTO3));
        userEntityList = new ArrayList<>();
        // Mock converter
        Mockito.when(userConverter.UserDTOToUser(userDTO1)).thenReturn(userEntity1);
        Mockito.when(userConverter.UserDTOToUser(userDTO2)).thenReturn(userEntity2);
        Mockito.when(userConverter.UserDTOToUser(userDTO3)).thenReturn(userEntity3);

        // Mock role
        Mockito.when(roleService.findByName(roleAdminEntity.getName())).thenReturn(roleAdminEntity);
        Mockito.when(roleService.findByName(roleUserEntity.getName())).thenReturn(roleUserEntity);

        for (UserDTO userDTO : userDTOList) {
            userEntityList.add(userConverter.UserDTOToUser(userDTO));
        }
    }

    @Test
    public void testFindAll_200() throws Exception {
        Page entityPage = Mockito.mock(Page.class);
        Pageable pageable = PageRequest.of(0, 3);
        Mockito.when(userRepository.findAll(pageable)).thenReturn(entityPage);
        Mockito.when(userRepository.findAll(pageable).getContent()).thenReturn(userEntityList);

        List<UserEntity> expectedResult = userRepository.findAll(pageable).getContent();
        List<UserDTO> expectedResultConverter = new ArrayList<>();
        for (UserEntity userEntity : expectedResult)
            expectedResultConverter.add(userConverter.UserToUserDTO(userEntity));

        List<UserDTO> actualResult = userService.findAll(pageable);

        assertEquals(expectedResultConverter, actualResult);
        assertEquals(actualResult.size(), 3);
    }

//    @Test
//    public void testFindByFilters_200() {
//        String fullname = "ngan";
//        String phone = "";
//        Boolean enabled = true;
//        Date createFrom = null;
//        Date createTo = null;
//        Pageable pageable = PageRequest.of(0, 3);
//
//        Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
//
//        ResponseFilter<UserDTO> responseFilter = userService.findByFilters(pageable, fullname, phone, enabled, createFrom, createTo);
//
//        assertEquals(3, responseFilter.data().size());
//        assertEquals("ngan", responseFilter.data().get(0).getFullName());
//    }

    @Test
    public void testFindByUsername_200() throws Exception {
        String username = "nganvo@gmail.com";
        Mockito.when(userRepository.findUserEntityByUsername(username)).thenReturn(userEntityList.get(0));

        UserDTO expectedResult = userConverter.UserToUserDTO(userRepository.findUserEntityByUsername(username));
        UserDTO actualResult = userService.findByUsername(username);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testFindByUsername_404() {
        String username = "nganvo33@gmail.com";
        Mockito.when(userRepository.findUserEntityByUsername(username)).thenReturn(null).thenThrow(CustomException.NotFoundException.class);
        assertThrows(CustomException.NotFoundException.class, () -> userService.findByUsername(username));
    }

    @Test
    public void testCreateUser_200() {
        UserDTO newUserDTO = UserDTO.builder().username("nganvo4@gmail.com").password("123456").address("BenTre").avatar(null).fullName("phuc ngan").phone("12345678").build();
        UserEntity newUserEntity = UserEntity.builder().username("nganvo4@gmail.com").password("123456").address("BenTre").avatar(null).fullName("phuc ngan").phone("12345678").roleEntities(new ArrayList<>()).commentEntityList(new ArrayList<>()).postEntityList(new ArrayList<>()).build();
        //Mock save
        Mockito.when(userRepository.save(newUserEntity)).thenReturn(newUserEntity);
        //Mock converter
        Mockito.when(userConverter.UserToUserDTO(newUserEntity)).thenReturn(newUserDTO);
        Mockito.when(userConverter.UserDTOToUser(newUserDTO)).thenReturn(newUserEntity);
        //Mock email service
        Mockito.when(emailService.generateRandomString(50)).thenReturn("f9K4bRF8zDBjlyuyTJV4ISRhm0G0x1T6LPPgp6LK5VS6mJ3N55");

        UserDTO expectedResult = userConverter.UserToUserDTO(userRepository.save(newUserEntity));
        UserDTO actualResult = userService.save(newUserDTO, TypesLogin.NORMAL);

        assertEquals(expectedResult, actualResult);
        assertEquals(expectedResult.getUsername(), actualResult.getUsername());
    }

    @Test
    public void testUpdateUser_200() {
        UserDTO userDTOUpdate = new UserDTO(
                1L,
                "admin",
                new Date(),
                "admin",
                new Date(),
                "nganvo@gmail.com",
                "123Abc",
                "phuc ngan update",
                "12345678",
                "BenTre",
                null,
                true,
                new ArrayList<>(Arrays.asList("ROLE_USER", "ROLE_ADMIN")));
        UserEntity userEntityUpdate = new UserEntity(
                1L,
                "admin",
                new Date(),
                "admin",
                new Date(),
                "nganvo@gmail.com",
                "123456",
                "phuc ngan update",
                null,
                "12345678",
                "BenTre",
                1,
                null,
                true,
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList(roleAdminEntity, roleUserEntity)), new ArrayList<>());

        String username = "nganvo@gmail.com";

        UserUpdateDTO userFormUpdateDTO = new UserUpdateDTO();
        userFormUpdateDTO.setAddress("BenTre");
        userFormUpdateDTO.setId(1L);
        userFormUpdateDTO.setFullName("phuc ngan update");
        userFormUpdateDTO.setPhone("12345678");
        userFormUpdateDTO.setCreatedBy("admin");
        userFormUpdateDTO.setCreatedDate(new Date());
        userFormUpdateDTO.setUpdatedBy("admin");
        userFormUpdateDTO.setUpdatedDate(new Date());

        Mockito.when(userRepository.findUserEntityByUsername(username)).thenReturn(userEntityList.get(0));
        Mockito.when(userConverter.UpdateInfo_UserDTOToUser(userFormUpdateDTO, userEntityList.get(0))).thenReturn(userEntityUpdate);
        Mockito.when(userRepository.save(userEntityUpdate)).thenReturn(userEntityUpdate);
        Mockito.when(userConverter.UserToUserDTO(userEntityUpdate)).thenReturn(userDTOUpdate);

        UserDTO expectedResult = userConverter.UserToUserDTO(userRepository.save(userEntityUpdate));
        UserDTO actualResult = userService.update(userFormUpdateDTO, username);

        assertEquals(expectedResult, actualResult);


    }

    @Test
    public void testUpdateUser_notFoundUser_404() {
        String username = "nganvo3333@gmail.com";

        UserUpdateDTO userFormUpdateDTO = new UserUpdateDTO();
        userFormUpdateDTO.setAddress("BenTre");
        userFormUpdateDTO.setId(1L);
        userFormUpdateDTO.setFullName("phuc ngan update");
        userFormUpdateDTO.setPhone("12345678");
        userFormUpdateDTO.setCreatedBy("admin");
        userFormUpdateDTO.setCreatedDate(new Date());
        userFormUpdateDTO.setUpdatedBy("admin");
        userFormUpdateDTO.setUpdatedDate(new Date());

        Mockito.when(userRepository.findUserEntityByUsername(username)).thenReturn(null).thenThrow(CustomException.NotFoundException.class);

        assertThrows(CustomException.NotFoundException.class, () -> userService.update(userFormUpdateDTO, username));
    }

    @Test
    public void testUpdatePassword_200() {
        UserUpdatePasswordDTO userUpdatePasswordDTO = new UserUpdatePasswordDTO();
        userUpdatePasswordDTO.setNewPassword("123456");
        userUpdatePasswordDTO.setOldPassword("123Abc");
        String username = "nganvo@gmail.com";

        UserDTO userDTOUpdate = new UserDTO(
                1L,
                "admin",
                new Date(),
                "admin",
                new Date(),
                "nganvo@gmail.com",
                "$2a$12$bCI3xofMqmCWEGk/iv2axe/uTgjkLSaIAWv7UWbEU9iBxZU/d4ksO",
                "phuc ngan update",
                "12345678",
                "BenTre",
                null,
                true,
                new ArrayList<>(Arrays.asList("ROLE_USER", "ROLE_ADMIN")));
        UserEntity userEntityUpdate = new UserEntity(
                1L,
                "admin",
                new Date(),
                "admin",
                new Date(),
                "nganvo@gmail.com",
                "$2a$12$bCI3xofMqmCWEGk/iv2axe/uTgjkLSaIAWv7UWbEU9iBxZU/d4ksO",
                "phuc ngan update",
                null,
                "12345678",
                "BenTre",
                1,
                null,
                true,
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList(roleAdminEntity, roleUserEntity)), new ArrayList<>());

        Mockito.when(userRepository.findUserEntityByUsername(username)).thenReturn(userEntityList.get(0));

        Mockito.when(passwordEncoder.matches(userUpdatePasswordDTO.getOldPassword(), userEntityList.get(0).getPassword())).thenReturn(true);

        Mockito.when(userRepository.save(userEntityUpdate)).thenReturn(userEntityUpdate);

        Mockito.when(userConverter.UserToUserDTO(userEntityUpdate)).thenReturn(userDTOUpdate);

        userService.updatePassword(userUpdatePasswordDTO, username);

        UserDTO expectedResult = userConverter.UserToUserDTO(userRepository.findUserEntityByUsername(username));
        UserDTO actualResult = userService.findByUsername(username);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testUpdatePassword_notFound_404() {
        UserUpdatePasswordDTO userUpdatePasswordDTO = new UserUpdatePasswordDTO();
        userUpdatePasswordDTO.setNewPassword("123456");
        userUpdatePasswordDTO.setOldPassword("123Abc");
        String username = "nganvo33333@gmail.com";

        Mockito.when(userRepository.findUserEntityByUsername(username)).thenReturn(null).thenThrow(CustomException.NotFoundException.class);

        assertThrows(CustomException.NotFoundException.class, () -> userService.updatePassword(userUpdatePasswordDTO, username));
    }

    @Test
    public void testUpdatePassword_passwordNotMatches_400() {
        UserUpdatePasswordDTO userUpdatePasswordDTO = new UserUpdatePasswordDTO();
        userUpdatePasswordDTO.setNewPassword("123456");
        userUpdatePasswordDTO.setOldPassword("123Abccccc");
        String username = "nganvo33333@gmail.com";

        Mockito.when(userRepository.findUserEntityByUsername(username)).thenReturn(userEntityList.get(0));
        Mockito
                .when(passwordEncoder.matches(userUpdatePasswordDTO.getOldPassword(), userEntityList.get(0).getPassword()))
                .thenReturn(false)
                .thenThrow(CustomException.BadRequestException.class);

        assertThrows(CustomException.BadRequestException.class, () -> userService.updatePassword(userUpdatePasswordDTO, username));
    }

    @Test
    public void testCreateUser_emailExist_400() {
        UserDTO newUserDTO = UserDTO.builder().username("nganvo@gmail.com").password("123456").address("BenTre").avatar(null).fullName("phuc ngan").phone("12345678").build();
        UserEntity newUserEntity = UserEntity.builder().username("nganvo@gmail.com").password("123456").address("BenTre").avatar(null).fullName("phuc ngan").phone("12345678").roleEntities(new ArrayList<>()).commentEntityList(new ArrayList<>()).postEntityList(new ArrayList<>()).build();

        //Mock find user exist
        Mockito.when(userRepository.findUserEntityByUsername(newUserDTO.getUsername())).thenReturn(newUserEntity);
        //Mock save to thor
        Mockito.when(userRepository.save(newUserEntity)).thenReturn(null).thenThrow(CustomException.BadRequestException.class);

        assertThrows(CustomException.BadRequestException.class, () -> userService.save(newUserDTO, TypesLogin.NORMAL));
    }

    @Test
    public void testFindByFilters() {
        assertTrue(true);
    }
}
