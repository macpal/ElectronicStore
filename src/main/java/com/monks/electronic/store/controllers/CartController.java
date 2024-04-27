package com.monks.electronic.store.controllers;

import com.monks.electronic.store.dtos.AddItemToCartRequest;
import com.monks.electronic.store.dtos.ApiResponseMessage;
import com.monks.electronic.store.dtos.CartDTO;
import com.monks.electronic.store.services.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /* Add items to cart */
    @PostMapping("/{userId}")
    public ResponseEntity<CartDTO> addItemToCart(@RequestBody AddItemToCartRequest request, @PathVariable String userId) {
        CartDTO cartDTO = cartService.addItemToCart(userId, request);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponseMessage> removeItemFromCart(@PathVariable String userId, @PathVariable int itemId) {
        cartService.removeItemFromCart(userId, itemId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message(itemId+" item is removed !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Cart is emptied for user "+ userId+" !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable String userId) {
        CartDTO cartDTO = cartService.getCartByUser(userId);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }
}
