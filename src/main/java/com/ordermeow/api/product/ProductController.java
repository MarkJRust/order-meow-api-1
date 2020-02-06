package com.ordermeow.api.product;

import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@Api(tags = {"Product API"})
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @ApiOperation(value = "Create Product", notes = "Creates a product in our database with the given name")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = ProductEntity.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Object.class)
    })
    @PostMapping("/product")
    public ProductEntity createProduct(
            @ApiParam(name = "ProductEntity", value = "product", required = true, format = MediaType.APPLICATION_JSON_VALUE, example = "{productName: \"Pizza\"")
            @RequestBody ProductEntity product) {

        return productService.createProduct(product);
    }

    @GetMapping("/product/{productId}")
    public ProductEntity getProduct(
            @PathVariable Long productId) {

        return productService.getProduct(productId);
    }

    @DeleteMapping("/product/{productId}")
    public String deleteByProductId(
            @PathVariable Long productId) {
        productService.deleteProductById(productId);
        return "";
    }
}
