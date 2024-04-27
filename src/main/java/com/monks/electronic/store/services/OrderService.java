package com.monks.electronic.store.services;

import com.monks.electronic.store.dtos.CreateOrderRequest;
import com.monks.electronic.store.dtos.OrderDTO;
import com.monks.electronic.store.dtos.PageableResponse;

import java.util.List;

public interface OrderService {

    /* Create order */
    OrderDTO createOrder(CreateOrderRequest orderDTO);

    /* Remove order */
    void removeOrder(String orderId);

    /* Get orders of a user */
    List<OrderDTO> getOrdersOfUser(String userId);

    /* Get orders */
    PageableResponse<OrderDTO> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir);

}
