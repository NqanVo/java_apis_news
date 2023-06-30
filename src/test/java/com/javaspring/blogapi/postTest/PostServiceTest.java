package com.javaspring.blogapi.postTest;
import static org.junit.jupiter.api.Assertions.*;
import com.javaspring.blogapi.controller.PostController;
import com.javaspring.blogapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito.*;
@WebMvcTest(controllers = PostController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Autowired
    MockMvc mockMvc;

    @Mock
    UserRepository userRepository;


    @BeforeEach
    void setUp() throws Exception {

    }

    @Test
    public void testFindAllPost() throws Exception {

    }
}
