package com.evershine.EvershineServer.securityConf.service;

import com.evershine.EvershineServer.securityConf.dto.AuthResponse;
import com.evershine.EvershineServer.securityConf.dto.LoginRequestDto;
import com.evershine.EvershineServer.securityConf.dto.RegisterRequestDto;
import com.evershine.EvershineServer.securityConf.dto.RegisterResponseDto;
import com.evershine.EvershineServer.securityConf.entity.AppUser;
import com.evershine.EvershineServer.securityConf.entity.Role;
import com.evershine.EvershineServer.securityConf.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;


    public RegisterResponseDto register(RegisterRequestDto request) {
        if (repository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Username already exists");
        }

        AppUser user = new AppUser();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAddress(request.getAddress());
        user.setPhoneNum(request.getPhoneNum());
//        user.setRole(Role.USER);  // role added when object instanciated

        user = repository.save(user);

        RegisterResponseDto dto = new RegisterResponseDto(
                user.getUsername(),
                user.getPhoneNum(),
                user.getId()
        );

        return dto;
    }

    public AuthResponse login(LoginRequestDto request) {

        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));

            UserDetails user =
                    (UserDetails) authentication.getPrincipal();

            String token = jwtService.generateToken(user);

            return new AuthResponse(token, user.getUsername());

        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password");
        }
    }
}
