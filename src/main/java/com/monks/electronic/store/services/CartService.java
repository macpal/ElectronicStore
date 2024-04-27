package com.monks.electronic.store.services;

import com.monks.electronic.store.dtos.AddItemToCartRequest;
import com.monks.electronic.store.dtos.CartDTO;

public interface CartService {
    /*Add items to cart*/
    /*If cart for user not available then create the cart and add the cart to user*/

    CartDTO addItemToCart(String userId, AddItemToCartRequest addItemToCartRequest);

    /*Remove item from cart*/
    void removeItemFromCart(String userId, int cartItem);

    void clearCart(String userId);
    CartDTO getCartByUser(String userId);

}
