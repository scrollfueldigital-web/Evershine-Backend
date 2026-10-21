package com.evershine.EvershineServer.CartApi.controller;

import com.evershine.EvershineServer.CartApi.dto.CartRequestDto;
import com.evershine.EvershineServer.CartApi.dto.CartResponseDto;
import com.evershine.EvershineServer.CartApi.dto.CartUpdateRequestDto;
import com.evershine.EvershineServer.CartApi.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponseDto> addToCart(@RequestBody CartRequestDto dto){
        return ResponseEntity.ok(cartService.addToCart(dto));
    }

    @Operation(summary = "Send user id to get cart associated to user", description = "user id needed")
    @GetMapping("/{id}")
    public ResponseEntity<List<CartResponseDto>> getAllCartAssociatedToUser(@PathVariable UUID id){
        return ResponseEntity.ok(cartService.getAllCartAssociatedToUser(id));
    }

    @Operation(summary = "Update cart with cart id, only can update quantity and attributes")
    @PutMapping("/{id}")
    public ResponseEntity<CartResponseDto> updateCartWithId(@PathVariable UUID id, @RequestBody CartUpdateRequestDto dto){
        return ResponseEntity.ok(cartService.updateCartById(id,dto));
    }


    @Operation(summary = "Enter cart id only")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCartById(@PathVariable UUID id){
        return ResponseEntity.ok(cartService.deleteById(id));
    }
}
