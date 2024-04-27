package com.monks.electronic.store.services;

import com.monks.electronic.store.dtos.PageableResponse;
import com.monks.electronic.store.dtos.ProductDto;

import java.util.Collection;

public interface ProductService {

    /*Create Product*/
    ProductDto create(ProductDto productDto);

    /*Update Product*/
    ProductDto update(ProductDto productDto, String productId);

    /*Delete Product*/
    void delete(String productId);

    /*Get Single Product*/
    ProductDto get(String productId);

    /*Get All Products*/
    PageableResponse<ProductDto> getAll(int pageNumber ,int pageSize, String sortBy, String sortDir);

    /*Get All Live Products*/
    PageableResponse<ProductDto> getAllLive(int pageNumber ,int pageSize, String sortBy, String sortDir);

    /*Search Products*/
    PageableResponse<ProductDto> searchByTitle(String subtitle, int pageNumber ,int pageSize, String sortBy, String sortDir);


    /*Search Products with price less than*/
    PageableResponse<ProductDto> searchByPriceLessThanEqual(int priceLessThan, int pageNumber ,int pageSize, String sortBy, String sortDir);

    /*Create product wth given category id*/
    ProductDto createWithCategory(ProductDto productDto, String categoryId);

    /* Update product Id with category Id */
    ProductDto updateCategory(String productId, String categoryId);
}
