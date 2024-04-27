package com.monks.electronic.store.entities;

import lombok.*;
import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int orderItemId;

    private int quantity;
    private int totalPrice;
    @OneToOne
    private Product product;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
