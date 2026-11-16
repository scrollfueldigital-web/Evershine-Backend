package com.evershine.EvershineServer.securityConf.controller;

import com.evershine.EvershineServer.securityConf.dto.AppUserResponseDto;
import com.evershine.EvershineServer.securityConf.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping("/info")
    public ResponseEntity<AppUserResponseDto> getUserInfo(@AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        return ResponseEntity.ok(appUserService.getAppUserInfo(username));
    }
}
