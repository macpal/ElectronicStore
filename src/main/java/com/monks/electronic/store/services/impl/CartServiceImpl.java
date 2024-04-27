package com.monks.electronic.store.services.impl;

import com.monks.electronic.store.dtos.AddItemToCartRequest;
import com.monks.electronic.store.dtos.CartDTO;
import com.monks.electronic.store.dtos.ProductDto;
import com.monks.electronic.store.dtos.UserDTO;
import com.monks.electronic.store.entities.Cart;
import com.monks.electronic.store.entities.CartItem;
import com.monks.electronic.store.entities.Product;
import com.monks.electronic.store.entities.User;
import com.monks.electronic.store.exceptions.BadApiRequest;
import com.monks.electronic.store.exceptions.ResourceNotFound;
import com.monks.electronic.store.repositories.CartItemRepository;
import com.monks.electronic.store.repositories.CartRepository;
import com.monks.electronic.store.repositories.ProductRepository;
import com.monks.electronic.store.repositories.UserRepository;
import com.monks.electronic.store.services.CartService;
import com.monks.electronic.store.services.ProductService;
import com.monks.electronic.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.BackingStoreException;

@Service
public class CartServiceImpl implements CartService {

    private ProductRepository productRepository;
    private ProductService productService;
    private UserRepository userRepository;
    private UserService userService;
    private CartRepository cartRepository;
    private CartItemRepository cartItemRepository;
    private ModelMapper modelMapper;
    private Logger logger;

    public CartServiceImpl(ProductRepository productRepository, ProductService productService, UserRepository userRepository, UserService userService, CartRepository cartRepository, CartItemRepository cartItemRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.productService = productService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper = modelMapper;
        logger = LoggerFactory.getLogger(CartServiceImpl.class);
    }

    @Override
    public CartDTO addItemToCart(String userId, AddItemToCartRequest request) {
        int quantity = request.getQuantity();
        String productId = request.getProductId();

        if(quantity<=0) {
            throw new BadApiRequest("Requested quantity is not valid !!");
        }

        /*Get the product*/
        ProductDto productDto = productService.get(productId);
        Product product = modelMapper.map(productDto, Product.class);

        /*Fetch User*/
        UserDTO userDTO = userService.getUserById(userId);
        User user = modelMapper.map(userDTO, User.class);

        Cart cart = null;
        try {
            cart =cartRepository.findByUser(user).get();
        } catch(NoSuchElementException e) {
            cart = new Cart();
            cart.setCartId(UUID.randomUUID().toString());
            cart.setCreatedAt(new Date());
        }

        /* Perform cart operation */
        /* If cart item already present, then update */
        AtomicBoolean updated = new AtomicBoolean(false);
        List<CartItem> items = cart.getItems();
        items.stream().map(item -> {
            if (item.getProduct().getProductId().equals(productId)) {
                /* Item already present in cart */
                item.setQuantity(quantity);
                item.setTotalPrice(quantity * product.getDiscountedPrice());
                updated.set(true);
            }
            return item;
        });

        /*Create items*/
        logger.info("Create item");
        if(!updated.get()) {
            CartItem cartItem = CartItem.builder()
                    .quantity(quantity)
                    .totalPrice(quantity * productDto.getDiscountedPrice())
                    .cart(cart)
                    .product(product)
                    .build();
            logger.info("cartItem: {}", cartItem);
            logger.info("cart.getItems(): {}", cart.getItems());

            items.add(cartItem);
        }

        cart.setUser(user);
        Cart updatedCart = cartRepository.save(cart);

        return modelMapper.map(updatedCart, CartDTO.class);
    }

    @Override
    public void removeItemFromCart(String userId, int cartItem) {
        CartItem cartItem1 = cartItemRepository.findById(cartItem).orElseThrow(() -> new ResourceNotFound("Cart Item not found in DB"));
        cartItemRepository.delete(cartItem1);
    }

    @Override
    public void clearCart(String userId) {
        /*Fetch user*/
        UserDTO userDTO = userService.getUserById(userId);
        User user = modelMapper.map(userDTO, User.class);

        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFound("Cart of user not found !!"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public CartDTO getCartByUser(String userId) {
        /*Fetch user*/
        UserDTO userDTO = userService.getUserById(userId);
        User user = modelMapper.map(userDTO, User.class);

        Cart cartByUser = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFound("Cart of user not found !!"));
        return modelMapper.map(cartByUser, CartDTO.class);
    }

}
