package com.monks.electronic.store.controllers;

import com.monks.electronic.store.dtos.*;
import com.monks.electronic.store.services.CategoryService;
import com.monks.electronic.store.services.FileService;
import com.monks.electronic.store.services.ProductService;


import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final FileService fileService;

    @Value("${category.profile.image.path}")
    private String imageUploadPath;

    Logger logger = LoggerFactory.getLogger(CategoryController.class);

    /*Contructor*/
    public CategoryController(CategoryService categoryService, ProductService productService, FileService fileService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.fileService = fileService;
    }

    /*Create*/
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(categoryService.create(categoryDTO));
    }

    /*Update*/
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable("categoryId") String categoryId) {
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body( categoryService.update(categoryDTO, categoryId));
    }

    /*Delete*/
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponseMessage> deleteCategory(@PathVariable("categoryId") String categoryId){
        categoryService.delete(categoryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseMessage.builder().message("Category is deleted successfully.").success(true).status(HttpStatus.OK).build());
    }

    /*Get All*/
    @GetMapping
    public ResponseEntity<PageableResponse<CategoryDTO>> getAllCategories(@RequestParam(value = "pageNumber", required = false,defaultValue = "0") int pageNumber,
                                                                          @RequestParam(value = "pageSize", required = false,defaultValue = "10") int pageSize,
                                                                          @RequestParam(value = "sortBy", required = false, defaultValue = "title") String sortBy,
                                                                          @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir) {
        PageableResponse<CategoryDTO> pageableResponse = categoryService.getAll(pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pageableResponse);
    }

    /*Get Single*/
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryId(@PathVariable("categoryId") String categoryId) {
        CategoryDTO categoryDTO = categoryService.get(categoryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryDTO);
    }

    //upload category image
    @PutMapping ("/image/{categoryId}")
    public ResponseEntity<ImageResponse> uploadUserImage(@RequestParam("userImage") MultipartFile image, @PathVariable String categoryId) throws IOException {
        String imageName = fileService.uploadImage(image, imageUploadPath);

        /* update category name */
        CategoryDTO categoryDTO = categoryService.get(categoryId);
        categoryDTO.setCoverImage(imageName);
        categoryService.update(categoryDTO,categoryId);

        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(imageName)
                .success(true)
                .status(HttpStatus.CREATED).build();
        return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);
    }

    //serve category image
    @GetMapping("image/{categoryId}")
    public void serveUserImage(@PathVariable String categoryId, HttpServletResponse response) throws IOException {
        CategoryDTO categoryDTO = categoryService.get(categoryId);
        logger.info("Category image name: {} ", categoryDTO.getCoverImage());
        InputStream resource = fileService.getResource(imageUploadPath, categoryDTO.getCoverImage());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }

    /*Create product wth given category id*/
    @PostMapping("/{categoryId}/product")
    public ResponseEntity<ProductDto> createProductWithCategory(@PathVariable String categoryId, @RequestBody ProductDto productDto) {
        ProductDto productWithCategory = productService.createWithCategory(productDto, categoryId);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDto);
    }

    /* Update category of product */
    @PutMapping("/{categoryId}/product/{productId}")
    public ResponseEntity<ProductDto> updateCategoryOfProduct(@PathVariable String productId,@PathVariable  String categoryId) {
        ProductDto productDto = productService.updateCategory(productId, categoryId);
        return ResponseEntity.status(HttpStatus.OK).body(productDto);
    }
}
