package com.evershine.EvershineServer.CartApi.repository;

import com.evershine.EvershineServer.CartApi.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
}
