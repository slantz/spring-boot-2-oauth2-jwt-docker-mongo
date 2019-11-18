package com.yourproject.auth.service;

import com.yourproject.auth.error.MissingIdException;
import com.yourproject.auth.model.mongo.User;
import com.yourproject.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("userService")
public class UserServiceImpl implements UserDetailsService, UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("username not found:" + username));
    }

    @Override
    public List<User> getAll() {
        return this.userRepository.findAll();
    }

    @Override
    public User getById(String id) {
        return this.userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("user not found by id:" + id));
    }

    @Override
    public User getByUsername(String username) {
        return this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user not found by id:" + username));
    }

    @Override
    public List<GrantedAuthority> getAuthoritiesByUsername(String username) {
        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user not found by id:" + username));
        return List.copyOf(user.getAuthorities());
    }

    @Override
    public User signUp(User user) {
        User newUser =
                User
                        .copyFrom(user)
                        .authorities("USER")
                        .passwordEncoderFunction(passwordEncoder::encode)
                        .build();

        return this.userRepository.save(newUser);
    }

    @Override
    public List<User> createAll(List<User> users) {
        List<User> newUsers =
                users
                        .stream()
                        .map(user ->
                                     User
                                             .copyFrom(user)
                                             .passwordEncoderFunction(passwordEncoder::encode)
                                             .build())
                        .collect(Collectors.toList());

        return this.userRepository.saveAll(newUsers);
    }

    @Override
    public List<User> updateAll(List<User> users) {
        if (users.stream().anyMatch(user -> user.getUsername() == null)) {
            throw new MissingIdException("Some username is missing for bulk users update.");
        }

        return this.userRepository.findAndUpdateByUsernames(users);
    }

    @Override
    public boolean deleteById(String id) {
        this.userRepository.deleteById(id);
        return true;
    }
}
