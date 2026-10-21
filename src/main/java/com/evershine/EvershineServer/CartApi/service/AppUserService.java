package com.evershine.EvershineServer.CartApi.service;

import com.evershine.EvershineServer.CartApi.entity.AppUser;
import com.evershine.EvershineServer.CartApi.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;

    @Transactional
    public AppUser addUser(AppUser user){
        return appUserRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<AppUser> getUsers(){
        return appUserRepository.findAll();
    }
}
