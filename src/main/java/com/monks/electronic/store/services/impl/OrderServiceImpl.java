package com.monks.electronic.store.services.impl;

import com.monks.electronic.store.dtos.CreateOrderRequest;
import com.monks.electronic.store.dtos.OrderDTO;
import com.monks.electronic.store.dtos.PageableResponse;
import com.monks.electronic.store.entities.*;
import com.monks.electronic.store.exceptions.BadApiRequest;
import com.monks.electronic.store.exceptions.ResourceNotFound;
import com.monks.electronic.store.helper.ObjectListToPageableResponse;
import com.monks.electronic.store.helper.PageableHelper;
import com.monks.electronic.store.repositories.CartRepository;
import com.monks.electronic.store.repositories.OrderRepository;
import com.monks.electronic.store.repositories.UserRepository;
import com.monks.electronic.store.services.OrderService;
import com.monks.electronic.store.services.UserService;
import lombok.experimental.Helper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Service
public class OrderServiceImpl implements OrderService {

    private UserRepository userRepository;
    private UserService userService;
    private OrderRepository orderRepository;
    private CartRepository cartRepository;
    private ModelMapper modelMapper;
    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);


    public OrderServiceImpl(UserRepository userRepository, OrderRepository orderRepository, ModelMapper modelMapper, UserService userService, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.cartRepository = cartRepository;
    }

    @Override
    public OrderDTO createOrder(CreateOrderRequest orderDTO) {
         /* Fetch User*/
        User user = modelMapper.map(userService.getUserById(orderDTO.getUserId()), User.class);

        /*Fetch cart*/
        Cart cart = cartRepository.findById(orderDTO.getCartId()).orElseThrow(() -> new ResourceNotFound("Cart with id " + orderDTO.getCartId() + " not found!!"));
        List<CartItem> cartItems = cart.getItems();

        if(cartItems.isEmpty()) {
            throw new BadApiRequest("0 or less number of items in cart!!");
        }

        Order order = Order.builder()
                .billingName(orderDTO.getBillingName())
                .billingPhone(orderDTO.getBillingPhone())
                .billingAddress(orderDTO.getBillingAddress())
                .orderedDate(new Date())
                .deliveredDate(null)
                .paymentStatus(orderDTO.getPaymentStatus())
                .orderStatus(orderDTO.getOrderStatus())
                .orderId(UUID.randomUUID().toString())
                .user(user)
                .build();

        //  not set yet orderItems, amount
        AtomicReference<Integer> totalOrderedAmount = new AtomicReference<>(0);

        List<OrderItem> orderItem = cartItems.stream().map(cartItem -> {
            /*Calculate total amount of all orders*/
            totalOrderedAmount.set(totalOrderedAmount.get()+cartItem.getTotalPrice());

            /*cart item to order item*/
            return OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .totalPrice(cartItem.getTotalPrice())
                    .order(order)
                    .build();
        }).toList();

        order.setOrderItems(orderItem);
        order.setOrderAmount(totalOrderedAmount.get());



        /* Save order*/
        logger.info("order prepared: {}", order);
        Order savedOrder = orderRepository.save(order);

        logger.info("Order item saved to database");

        /* Clear cart as items are moved to order */
        cart.getItems().clear();
        cartRepository.save(cart);

        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    @Override
    public void removeOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFound("Order with id " + orderId + " not found !!"));
        orderRepository.delete(order);
    }

    @Override
    public List<OrderDTO> getOrdersOfUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFound("User with id " + userId + " not found"));
        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).toList();
    }

    @Override
    public PageableResponse<OrderDTO> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Order> page = orderRepository.findAll(pageable);

        return ObjectListToPageableResponse.getPageableResponse(page, OrderDTO.class);
    }
}
