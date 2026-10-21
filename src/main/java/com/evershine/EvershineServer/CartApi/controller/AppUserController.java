package com.evershine.EvershineServer.CartApi.controller;

import com.evershine.EvershineServer.CartApi.entity.AppUser;
import com.evershine.EvershineServer.CartApi.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AppUserController {
    private final AppUserService appUserService;

    @PostMapping
    public ResponseEntity<AppUser> addUser(@RequestBody AppUser user){
        return ResponseEntity.ok(appUserService.addUser(user));
    }

    @GetMapping
    public ResponseEntity<List<AppUser>>getAll(){
        return ResponseEntity.ok(appUserService.getUsers());
    }
}
