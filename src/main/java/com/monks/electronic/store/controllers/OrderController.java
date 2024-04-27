package com.monks.electronic.store.controllers;

import com.monks.electronic.store.dtos.ApiResponseMessage;
import com.monks.electronic.store.dtos.CreateOrderRequest;
import com.monks.electronic.store.dtos.OrderDTO;
import com.monks.electronic.store.dtos.PageableResponse;
import com.monks.electronic.store.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        OrderDTO orderDTO = orderService.createOrder(createOrderRequest);
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponseMessage> removeOrder(@PathVariable String orderId) {
        orderService.removeOrder(orderId);
        ApiResponseMessage apiResponseMessage = ApiResponseMessage.builder().message("Order is removed !!").status(HttpStatus.OK).success(true).build();
        return new ResponseEntity<>(apiResponseMessage,HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersOfUsers(@PathVariable String userId) {
        List<OrderDTO> ordersOfUser = orderService.getOrdersOfUser(userId);
        return new ResponseEntity<>(ordersOfUser,HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<PageableResponse<OrderDTO>> getOrdersOfUsers(@RequestParam(defaultValue = "0") int pageNumber,
                                                                       @RequestParam(defaultValue = "10") int pageSize,
                                                                       @RequestParam(defaultValue = "billingName") String sortBy,
                                                                       @RequestParam(defaultValue = "asc") String sortDir) {
        PageableResponse<OrderDTO> orders = orderService.getOrders(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

}
