package com.evershine.EvershineServer.CartApi.controller;

import com.evershine.EvershineServer.CartApi.dto.CartRequestDto;
import com.evershine.EvershineServer.CartApi.dto.CartResponseDto;
import com.evershine.EvershineServer.CartApi.dto.CartUpdateRequestDto;
import com.evershine.EvershineServer.CartApi.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;



@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 1. ADD TO CART (Already fixed!)
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponseDto> addToCart(
            @RequestBody @Valid CartRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.addToCart(dto, userDetails.getUsername()));
    }

    // 2. GET CURRENT USER'S CART (Clean REST, No {id} in path)
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CartResponseDto>> getMyCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getAllCartAssociatedToUser(userDetails.getUsername()));
    }

    // 3. UPDATE CART ITEM
    @PutMapping("/{cartId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponseDto> updateCart(
            @PathVariable UUID cartId,
            @RequestBody @Valid CartUpdateRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.updateCartById(cartId, dto, userDetails.getUsername()));
    }

    // 4. DELETE CART ITEM
    @DeleteMapping("/{cartId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> deleteCart(
            @PathVariable UUID cartId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(Map.of("message", cartService.deleteById(cartId, userDetails.getUsername())));
    }
}

// @RestController
// @RequestMapping("/api/cart")
// @RequiredArgsConstructor
// public class CartController {
//     private final CartService cartService;

//     @PostMapping
//     @PreAuthorize("hasRole('USER')")
//     @Operation(summary = "Role USER")
//     public ResponseEntity<CartResponseDto> addToCart(@RequestBody @Valid CartRequestDto dto, @AuthenticationPrincipal UserDetails userDetails){
//         String emil = userDetails.getUsername();
//         return ResponseEntity.ok(cartService.addToCart(dto,emil));
//     }

//     @Operation(summary = "ROLE USER, Send user id to get cart associated to user", description = "user id needed")
//     @GetMapping("/{id}")
//     @PreAuthorize("hasRole('USER')")
//     public ResponseEntity<List<CartResponseDto>> getAllCartAssociatedToUser(@PathVariable UUID id){
//         return ResponseEntity.ok(cartService.getAllCartAssociatedToUser(id));
//     }

//     @Operation(summary = "ROLE USER, Update cart with cart id, only can update quantity and attributes")
//     @PutMapping("/{id}")
//     @PreAuthorize("hasRole('USER')")
//     public ResponseEntity<CartResponseDto> updateCartWithId(@PathVariable UUID id, @RequestBody @Valid CartUpdateRequestDto dto){
//         return ResponseEntity.ok(cartService.updateCartById(id,dto));
//     }


//     @Operation(summary = "ROLE USEREnter cart id only")
//     @DeleteMapping("/{id}")
//     @PreAuthorize("hasRole('USER')")
//     public ResponseEntity<String> deleteCartById(@PathVariable UUID id){
//         return ResponseEntity.ok(cartService.deleteById(id));
//     }
// }
