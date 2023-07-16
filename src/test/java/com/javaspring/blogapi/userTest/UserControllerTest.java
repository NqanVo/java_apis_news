package com.javaspring.blogapi.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.javaspring.blogapi.controller.UserController;
import com.javaspring.blogapi.converter.UserConverter;
import com.javaspring.blogapi.dto.user.UserDTO;
import com.javaspring.blogapi.model.UserEntity;
import com.javaspring.blogapi.repository.UserRepository;
import com.javaspring.blogapi.service.impl.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    private MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserConverter userConverter;
    @InjectMocks
    private UserController userController;
    private UserDTO userDTO1 = new UserDTO();
    private UserDTO userDTO2 = new UserDTO();
    private UserDTO userDTO3 = new UserDTO();

    @Before
    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//
//        userDTO1.setId(1L);
//        userDTO1.setUsername("nganvo");
//        userDTO1.setFullName("vo ngan");
//
//        userDTO2.setId(2L);
//        userDTO2.setUsername("nganvo1");
//        userDTO2.setFullName("vo ngan 1");
//
//        userDTO3.setId(3L);
//        userDTO3.setUsername("nganvo2");
//        userDTO3.setFullName("vo ngan 2");
    }

//    @Test
//    public void testFindByFilter_200() throws Exception {
//        List<UserDTO> list = new ArrayList<>(Arrays.asList(userDTO1, userDTO2, userDTO3));
//        List<UserEntity> entityList = new ArrayList<>();
//        for (UserDTO userDTO : list)
//            entityList.add(userConverter.UserDTOToUser(userDTO));
//
//        Mockito.when(userRepository.findByUsername("nganvo")).thenReturn(new UserEntity());
//
//        mockMvc
//                .perform(MockMvcRequestBuilders.get("/users/nganvo").contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(3)));
//    }
}
