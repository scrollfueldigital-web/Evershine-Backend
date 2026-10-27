package com.evershine.EvershineServer.securityConf.service;

import com.evershine.EvershineServer.securityConf.entity.AppUser;
import com.evershine.EvershineServer.securityConf.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser user = repository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: "+username));

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
