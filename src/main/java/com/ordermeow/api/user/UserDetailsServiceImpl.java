package com.ordermeow.api.user;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity applicationUser = userRepository.findUserEntityByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new User(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
    }

    public UserEntity createUser(UserEntity user) {
        if (userRepository.findUserEntityByUsername(user.getUsername()).isPresent()) {
            throw new UserExceptions.UserAlreadyExistsException(user.getUsername());
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}

