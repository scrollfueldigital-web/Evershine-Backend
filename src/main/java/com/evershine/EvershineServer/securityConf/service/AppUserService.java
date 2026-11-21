package com.evershine.EvershineServer.securityConf.service;

import com.evershine.EvershineServer.CartApi.repository.CartRepository;
import com.evershine.EvershineServer.securityConf.dto.AppUserResponseDto;
import com.evershine.EvershineServer.securityConf.dto.PageResponse;
import com.evershine.EvershineServer.securityConf.dto.UserUpdateDto;
import com.evershine.EvershineServer.securityConf.entity.AppUser;
import com.evershine.EvershineServer.securityConf.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final CartRepository cartRepository;

    @Transactional(readOnly = true)
    public AppUserResponseDto getAppUserInfo(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist!"));
        return getAppUserResponseDto(user);
    }

    @Transactional(readOnly = true)
    public PageResponse<AppUserResponseDto> getAllUsers(int size, int pageNo, String sortBy, boolean asc) {
        size = 10;
        pageNo = Math.max(pageNo, 0);
        Set<String> sortOptions = Set.of("name", "username", "phoneNum");
        Pageable page = null;
        if (sortBy != null && sortOptions.contains(sortBy)) {
            Sort sort = asc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            page = PageRequest.of(pageNo, size, sort);
        } else {
            Sort sort = asc ? Sort.by("name").ascending() : Sort.by("name").descending();
            page = PageRequest.of(pageNo, size, sort);
        }

        Page<AppUser> userPage = appUserRepository.findAll(page);
        Page<AppUserResponseDto> userPageDto = userPage.map(this::getAppUserResponseDto);
        return PageResponse.from(userPageDto);
    }

    @Transactional
    public AppUserResponseDto updateUser(String username, UserUpdateDto updateDto) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist!"));
        user.setName(updateDto.getName());
        user.setAddress(updateDto.getAddress());
        user.setPhoneNum(updateDto.getPhoneNum());
        user = appUserRepository.save(user);
        return getAppUserResponseDto(user);
    }

    @Transactional
    public Map<String,Boolean> deleteUser(String username){
        cartRepository.deleteByAppUser_Username(username);
        int status = appUserRepository.deleteByUsername(username);
        return Map.of("Status", status != 0);
    }

    private AppUserResponseDto getAppUserResponseDto(AppUser user) {
        return new AppUserResponseDto(
                user.getName(),
                user.getUsername(),
                user.getPhoneNum(),
                user.getAddress()
        );
    }
}
