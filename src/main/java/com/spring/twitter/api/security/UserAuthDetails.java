package com.spring.twitter.api.security;

import com.spring.twitter.api.models.user.UserModel;
import com.spring.twitter.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthDetails implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String password) throws UsernameNotFoundException {
        final UserModel user = userRepository.findByPassword(password);
        if (user == null) {
            throw new UsernameNotFoundException("User '" + password + "' not found");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .authorities(user.getName())
                .password(user.getPassword())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
