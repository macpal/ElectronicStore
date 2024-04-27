package com.monks.electronic.store.services.impl;

import com.monks.electronic.store.dtos.PageableResponse;
import com.monks.electronic.store.dtos.ProductDto;
import com.monks.electronic.store.entities.Category;
import com.monks.electronic.store.entities.Product;
import com.monks.electronic.store.exceptions.ResourceNotFound;
import com.monks.electronic.store.helper.ObjectListToPageableResponse;
import com.monks.electronic.store.helper.PageableHelper;
import com.monks.electronic.store.repositories.CategoryRepository;
import com.monks.electronic.store.repositories.ProductRepository;
import com.monks.electronic.store.services.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto create(ProductDto productDto) {
        String uuid = UUID.randomUUID().toString();
        productDto.setProductId(uuid);
        Product product = modelMapper.map(productDto, Product.class);
        productRepository.save(product);
        return productDto;
    }

    @Override
    public ProductDto update(ProductDto productDto, String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFound("Product to update is not found !!"));
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setDiscountedPrice(productDto.getDiscountedPrice());
        product.setQuantity(productDto.getQuantity());
        product.setAddedDate(productDto.getAddedDate());
        product.setLive(productDto.isLive());
        product.setStock(productDto.isStock());
        product.setProductImageName(productDto.getProductImageName());

        return modelMapper.map(productRepository.save(product), ProductDto.class);
    }

    @Override
    public void delete(String productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public ProductDto get(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFound("Product with given id not found !!"));
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getAll(int pageNumber ,int pageSize, String sortBy, String sortDir) {
        Page<Product> page = productRepository.findAll(PageableHelper.createPageable(pageNumber,pageSize,sortBy,sortDir));
        return ObjectListToPageableResponse.getPageableResponse(page,ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getAllLive(int pageNumber ,int pageSize, String sortBy, String sortDir) {
        Page<Product> page = productRepository.findByLiveTrue(PageableHelper.createPageable(pageNumber , pageSize, sortBy, sortDir));
        return ObjectListToPageableResponse.getPageableResponse(page,ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> searchByTitle(String subtitle, int pageNumber ,int pageSize, String sortBy, String sortDir) {
        Page<Product> page = productRepository.findByTitleContaining(subtitle, PageableHelper.createPageable(pageNumber , pageSize, sortBy, sortDir));
        return ObjectListToPageableResponse.getPageableResponse(page,ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> searchByPriceLessThanEqual(int priceLessThan, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<Product> page = productRepository.findByPriceLessThanEqual(priceLessThan, PageableHelper.createPageable(pageNumber , pageSize, sortBy, sortDir));
        return ObjectListToPageableResponse.getPageableResponse(page,ProductDto.class);
    }

    @Override
    public ProductDto createWithCategory(ProductDto productDto, String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFound("Category not found !!!"));
        String uuid = UUID.randomUUID().toString();
        productDto.setProductId(uuid);
        Product product = modelMapper.map(productDto, Product.class);
        /* Set category in product */
        product.setCategory(category);
        return modelMapper.map(productRepository.save(product), ProductDto.class);
    }

    @Override
    public ProductDto updateCategory(String productId, String categoryId) {
        /* Fetch product */
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFound("Product not found !!"));
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFound("Category not found !!"));
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);
    }
}
