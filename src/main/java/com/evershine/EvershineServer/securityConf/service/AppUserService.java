package com.evershine.EvershineServer.securityConf.service;

import com.evershine.EvershineServer.securityConf.dto.AppUserResponseDto;
import com.evershine.EvershineServer.securityConf.entity.AppUser;
import com.evershine.EvershineServer.securityConf.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;

    @Transactional(readOnly = true)
    public AppUserResponseDto getAppUserInfo(String username){
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User does not exist!"));
        return new AppUserResponseDto(
                user.getName(),
                user.getUsername(),
                user.getPhoneNum(),
                user.getAddress()
        );
    }
}
