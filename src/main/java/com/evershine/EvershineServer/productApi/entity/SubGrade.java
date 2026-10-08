package com.evershine.EvershineServer.productApi.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity @Setter @Getter @RequiredArgsConstructor
public class SubGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true) private String name;
    private Boolean active = true;
}

