package com.monks.electronic.store.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private String orderId;
    /* PENDING, DELIVERED, DISPATCHED*/
    /* ENUM */
    private String orderStatus;
    /* PAID, NOTPAID*/
    private String paymentStatus;
    private int orderAmount;

    @Column(length = 1000)
    private String billingAddress;
    private String billingPhone;
    private String billingName;
    private Date orderedDate;
    private Date deliveredDate;

    /*User*/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

}
