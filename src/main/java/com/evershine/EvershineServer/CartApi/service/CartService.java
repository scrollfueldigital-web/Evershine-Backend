package com.evershine.EvershineServer.CartApi.service;

import com.evershine.EvershineServer.CartApi.dto.CartAttributes;
import com.evershine.EvershineServer.CartApi.dto.CartRequestDto;
import com.evershine.EvershineServer.CartApi.dto.CartResponseDto;
import com.evershine.EvershineServer.CartApi.dto.CartUpdateRequestDto;
import com.evershine.EvershineServer.CartApi.entity.AppUser;
import com.evershine.EvershineServer.CartApi.entity.Cart;
import com.evershine.EvershineServer.CartApi.repository.AppUserRepository;
import com.evershine.EvershineServer.CartApi.repository.CartRepository;
import com.evershine.EvershineServer.productApi.entity.Product;
import com.evershine.EvershineServer.productApi.repository.ProductRepository;
import com.evershine.EvershineServer.projectExceptionHandler.CartQuantityException;
import com.evershine.EvershineServer.projectExceptionHandler.ProductNotFoundException;
import com.evershine.EvershineServer.projectExceptionHandler.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final AppUserRepository appUserRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartResponseDto addToCart(CartRequestDto cartDto) {
        // find user first
        AppUser user = appUserRepository.findById(cartDto.getAppUserId())
                .orElseThrow(() -> new ResourceNotFoundException("No user exist with id: " + cartDto.getAppUserId()));

        List<Cart> cartList = cartRepository.findCartWithProducts(cartDto.getAppUserId());

        if (cartDto.getQuantity() != null && cartDto.getQuantity() <= 0) {
            throw new RuntimeException("Quantity cannot be zero!");
        }

        // if attribute exist then increment quantity
        for (Cart c : cartList) {
            if (cartDto.getProductSlug() != null
                    && cartDto.getProductSlug().equals(c.getProduct().getSlug())
                    && isMatchedAttribute(c.getCartAttributes(), cartDto.getSelectedCartAttributes())
            ) {
                c.setQuantity(c.getQuantity() + cartDto.getQuantity());
//                cartRepository.save(c); dirty check will manage
                return toCartResponse(c);
            }
        }

        // check overflow
        if (!cartList.isEmpty() && cartList.size() >= 5) {
            throw new CartQuantityException("cartDto size overflow!");
        }

        // check product
        Product product = productRepository.findWithSlug(cartDto.getProductSlug())
                .orElseThrow(() -> new ProductNotFoundException("Product does not exist with slug: " + cartDto.getProductSlug()));

        //create cart
        Cart cart = toCart(cartDto, product, user);
        return toCartResponse(cartRepository.save(cart));

    }

    @Transactional(readOnly = true)
    public List<CartResponseDto> getAllCartAssociatedToUser(UUID userId) {
        List<Cart> cartList = cartRepository.findAllByUserIdWithProduct(userId);

        return cartList.stream()
                .map(this::toCartResponse)
                .toList();
    }

    @Transactional
    public CartResponseDto updateCartById(UUID cartId, CartUpdateRequestDto cartRequestDto) {
        if (cartRequestDto.getQuantity() != null && cartRequestDto.getQuantity() <= 0) {
            throw new CartQuantityException("Quantity must be greater than zero!");
        }

        // 1. Fetch user carts (already fetches products & brands eagerly)
        List<Cart> cartList = cartRepository.findCartWithProducts(cartRequestDto.getUserId());

        // 2. Identify the cart row being updated
        Cart targetCart = cartList.stream()
                .filter(c -> c.getId().equals(cartId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart does not exist with id: " + cartId));

        // 3. Find any OTHER carts that already match the target product + attributes
        List<Cart> duplicateCarts = new ArrayList<>();
        for (Cart c : cartList) {
            boolean isDifferentRow = !c.getId().equals(cartId);
            boolean isSameProduct = c.getProduct() != null
                    && cartRequestDto.getProductSlug() != null
                    && cartRequestDto.getProductSlug().equals(c.getProduct().getSlug());

            if (isDifferentRow && isSameProduct && isMatchedAttribute(c.getCartAttributes(), cartRequestDto.getCartAttributes())) {
                duplicateCarts.add(c);
            }
        }

        // 4. Calculate total merged quantity
        // Base quantity: either the newly requested quantity or targetCart's existing quantity + delta
        long totalQuantity = cartRequestDto.getQuantity() != null
                ? cartRequestDto.getQuantity()
                : targetCart.getQuantity();

        if (!duplicateCarts.isEmpty()) {
            // Sum up quantities from all conflicting rows
            long duplicatesTotal = duplicateCarts.stream()
                    .mapToLong(c -> c.getQuantity() != null ? c.getQuantity() : 0L)
                    .sum();
            totalQuantity += duplicatesTotal;

            // Delete the duplicate rows
            cartRepository.deleteAll(duplicateCarts);
        }

        // 5. Update target cart in-place (Hibernate dirty checking handles the UPDATE)
        targetCart.setCartAttributes(cartRequestDto.getCartAttributes());
        targetCart.setQuantity(totalQuantity);

        return toCartResponse(targetCart);
    }

    @Transactional
    public String deleteById(UUID cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new ResourceNotFoundException("Cart does not exist with id: " + cartId);
        }
        cartRepository.deleteById(cartId);

        return "Cart deleted with id: " + cartId;
    }

    private boolean isMatchedAttribute(List<CartAttributes> list1, List<CartAttributes> list2) {
        if (list1 == null && list2 == null) return true;
        if (list1 == null || list2 == null) return false;
        if (list1.size() != list2.size()) return false;

        // Convert cart's attributes to a Set of normalized keys: O(N)
        Set<String> set1 = list1.stream()
                .map(this::toNormalizedKey)
                .collect(Collectors.toSet());

        // Verify all DTO attributes exist in the set: O(M)
        for (CartAttributes attr : list2) {
            if (!set1.contains(toNormalizedKey(attr))) {
                return false;
            }
        }

        return true;
    }

    private String toNormalizedKey(CartAttributes attr) {
        if (attr == null) return "";
        String label = attr.getLabel() == null ? "" : attr.getLabel().trim().toLowerCase();
        String unit = attr.getUnit() == null ? "" : attr.getUnit().trim().toLowerCase();
        String value = attr.getValue() == null ? "" : attr.getValue().trim().toLowerCase();

        return label + "|" + unit + "|" + value;
    }

    private Cart toCart(CartRequestDto cartDto, Product p, AppUser user) {
        Cart cart = new Cart();
        cart.setCartAttributes(cartDto.getSelectedCartAttributes());
        cart.setQuantity(cartDto.getQuantity());
        cart.setProduct(p);
        cart.setAppUser(user);
        return cart;
    }

    private CartResponseDto toCartResponse(Cart c) {
        CartResponseDto response = new CartResponseDto();
        Product p = c.getProduct();

        if (p.getBrand() != null)
            response.setProductBrandId(p.getBrand().getId());

        response.setProductSlug(p.getSlug());
        response.setProductImageUrl(p.getImageUrl());
        response.setSelectedAttributes(c.getCartAttributes());
        response.setQuantity(c.getQuantity());
        response.setProductName(c.getProduct().getTitle());
        response.setExistingAttributes(p.getAttributes());
        response.setId(c.getId());
        return response;
    }
}
