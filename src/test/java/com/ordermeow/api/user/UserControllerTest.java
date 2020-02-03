package com.ordermeow.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordermeow.api.CustomGlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("local")
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new CustomGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void registerNewUser_success() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("username");
        user.setPassword("password");
        Mockito.when(userDetailsService.createUser(user)).thenReturn(user);
        post(objectMapper.writeValueAsString(user)).andExpect(status().isOk()).andReturn();
    }

    @Test
    void registerNewUser_userAlreadyExistsThrowsException() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("username");
        user.setPassword("password");
        Mockito.when(userDetailsService.createUser(user)).thenReturn(user);
        Mockito.when(userDetailsService.createUser(user)).thenThrow(new UserExceptions.UserAlreadyExistsException(user.getUsername()));
        post(objectMapper.writeValueAsString(user)).andExpect(status().isBadRequest()).andReturn();
    }

    private ResultActions post(String body) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post("/register")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }
}