package com.evershine.EvershineServer.CartApi.entity;

import com.evershine.EvershineServer.CartApi.dto.CartAttributes;
import com.evershine.EvershineServer.productApi.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity @Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser appUser;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<CartAttributes>cartAttributes;
}