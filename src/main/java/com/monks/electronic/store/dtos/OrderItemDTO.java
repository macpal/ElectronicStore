package com.monks.electronic.store.dtos;

import com.monks.electronic.store.entities.Order;
import com.monks.electronic.store.entities.Product;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class OrderItemDTO {
    private String orderItemId;
    private int quantity;
    private int totalPrice;
    private ProductDto product;
//    private OrderDTO order;
}
