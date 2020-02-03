package com.ordermeow.api.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {


    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Test
    void loadByUsername_throwsUsernameNotFound() {
        String username = "garbage";
        when(userRepository.findUserEntityByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
    }

    @Test
    void loadByUsername_success() {

        UserEntity expectedUser = createSimpleUserEntity();
        when(userRepository.findUserEntityByUsername(expectedUser.getUsername())).thenReturn(Optional.of(expectedUser));
        UserDetails actual = userDetailsService.loadUserByUsername(expectedUser.getUsername());

        Assertions.assertEquals(expectedUser.getUsername(), actual.getUsername());
        Assertions.assertEquals(expectedUser.getPassword(), actual.getPassword());
    }

    @Test
    void createUser_success() {
        String expectedPassword = "encoded-password";
        UserEntity user = createSimpleUserEntity();

        when(userRepository.findUserEntityByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(encoder.encode(user.getPassword())).thenReturn(expectedPassword);
        UserEntity actualUser = userDetailsService.createUser(user);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);

        Assertions.assertEquals(actualUser.getPassword(), expectedPassword);
    }

    @Test
    void createUser_usernameAlreadyExists() {
        UserEntity user = createSimpleUserEntity();

        when(userRepository.findUserEntityByUsername(user.getUsername())).thenReturn(Optional.of(user));
        Assertions.assertThrows(UserExceptions.UserAlreadyExistsException.class, () -> userDetailsService.createUser(user));
        Mockito.verify(userRepository, Mockito.times(0)).save(user);
    }

    private UserEntity createSimpleUserEntity() {
        UserEntity user = new UserEntity();
        String username = "this-is-a-great-test";
        String password = "this-is-a-great-password";
        user.setUsername(username);
        user.setPassword(password);
        user.setUserId((long) 1);
        return user;
    }
}