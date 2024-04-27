package com.monks.electronic.store.controllers;

import com.monks.electronic.store.dtos.*;
import com.monks.electronic.store.services.FileService;
import com.monks.electronic.store.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final FileService fileService;

    @Value("${product.profile.image.path}")
    private String imageUploadPath;
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService, FileService fileService) {
        this.productService = productService;
        this.fileService = fileService;
    }

    /*Create Product*/
    @PostMapping
    public ResponseEntity<ProductDto> getProduct(@RequestBody ProductDto productDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.create(productDto));
    }

    /*Update Product*/
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@RequestBody ProductDto productDto, @PathVariable String productId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.update(productDto,productId));

    }

    /*Delete Product*/
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable String productId) {
        productService.delete(productId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseMessage.builder().message("Product is deleted").status(HttpStatus.OK).success(true).build());
    }

    /*Get Single Product*/
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String productId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.get(productId));
    }

    /*Get All Products*/
    @GetMapping
    public ResponseEntity<PageableResponse<ProductDto>> getAllProducts(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                                                        @RequestParam(value = "sortBy", required = false, defaultValue = "productId") String sortBy,
                                                                        @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAll(pageNumber,pageSize,sortBy,sortDir));
    }

    /*Get All Live Products*/
    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDto>> getAllLiveProducts(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                                                        @RequestParam(value = "sortBy", required = false, defaultValue = "productId") String sortBy,
                                                                        @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllLive(pageNumber,pageSize,sortBy,sortDir));
    }

    /*Search Products by Title*/
    @GetMapping("title/{title}")
    public ResponseEntity<PageableResponse<ProductDto>> searchProductsByTitle(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                                                        @RequestParam(value = "sortBy", required = false, defaultValue = "productId") String sortBy,
                                                                        @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir,
                                                                        @PathVariable(value = "title") String title ){
        return ResponseEntity.status(HttpStatus.OK).body(productService.searchByTitle(title, pageNumber,pageSize,sortBy,sortDir));
    }

    /*Search Products with price less than*/
    @GetMapping("/priceLessThan/{priceLessThan}")
    public ResponseEntity<PageableResponse<ProductDto>> searchProductsByPriceLessThan(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                                                       @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                                                       @RequestParam(value = "sortBy", required = false, defaultValue = "productId") String sortBy,
                                                                       @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir,
                                                                       @PathVariable(value = "priceLessThan") int priceLessThan ){
        return ResponseEntity.status(HttpStatus.OK).body(productService.searchByPriceLessThanEqual(priceLessThan, pageNumber,pageSize,sortBy,sortDir));
    }

    //upload product image
    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponse> uploadUserImage(@RequestParam("productImage") MultipartFile image, @PathVariable String productId) throws IOException {
        String imageName = fileService.uploadImage(image, imageUploadPath);

        /* update image name */
        ProductDto productDto = productService.get(productId);
        productDto.setProductImageName(imageName);
        productService.update(productDto,productId);

        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(imageName)
                .success(true)
                .status(HttpStatus.CREATED).build();
        return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);
    }

    //serve product image
    @GetMapping("/image/{productId}")
    public void serveUserImage(@PathVariable(name = "productId") String productId, HttpServletResponse response) throws IOException {
        ProductDto productDto = productService.get(productId);
        logger.info("User product name: {} ", productDto.getProductImageName());
        InputStream resource = fileService.getResource(imageUploadPath, productDto.getProductImageName());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }
}
