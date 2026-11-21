package com.evershine.EvershineServer.securityConf.controller;

import com.evershine.EvershineServer.securityConf.dto.AppUserResponseDto;
import com.evershine.EvershineServer.securityConf.dto.PageResponse;
import com.evershine.EvershineServer.securityConf.dto.UserUpdateDto;
import com.evershine.EvershineServer.securityConf.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DEVELOPER')")
    public ResponseEntity<AppUserResponseDto> getUserInfo(@AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        return ResponseEntity.ok(appUserService.getAppUserInfo(username));
    }

    @GetMapping("/available-users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<PageResponse<AppUserResponseDto>> getAvailableUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    ){
        return ResponseEntity.ok(appUserService.getAllUsers(size,page,sortBy,ascending));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('USER', 'DEVELOPER')")
    public ResponseEntity<AppUserResponseDto> updateUser(@AuthenticationPrincipal UserDetails userDetails,@RequestBody @Valid UserUpdateDto dto){
        String username = userDetails.getUsername();
        return ResponseEntity.ok(appUserService.updateUser(username,dto));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Map<String,Boolean>> deleteUser(@AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        return ResponseEntity.ok(appUserService.deleteUser(username));
    }
}
