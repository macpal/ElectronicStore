package com.monks.electronic.store.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CartItemDTO {
    private int cartItemId;
    private ProductDto product;
    private int quantity;
    private int totalPrice;
}
