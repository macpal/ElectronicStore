package com.monks.electronic.store.dtos;

import com.monks.electronic.store.entities.CartItem;
import com.monks.electronic.store.entities.User;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CartDTO {
    private String cartId;
    private Date createdAt;
    private User user;
    private List<CartItemDTO> items = new ArrayList<>();
}
