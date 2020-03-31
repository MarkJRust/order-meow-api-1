package com.ordermeow.api.product;

import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping()
@Api(tags = {"Product API"})
@CrossOrigin
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @ApiOperation(value = "Create Product", notes = "Creates a product in our database with the given name")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = ProductEntity.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Object.class),
            @ApiResponse(code = 403, message = "Unauthorized", response = Object.class)
    })
    @PostMapping("/product")
    public ProductEntity createProduct(
            @ApiParam(name = "ProductEntity", value = "product", required = true, format = MediaType.MULTIPART_FORM_DATA_VALUE)
            @ModelAttribute ProductEntity product,
            @RequestParam(required = false) MultipartFile file
    ) {
        return productService.createProduct(product, file);
    }

    @ApiOperation(value = "Edit Product", notes = "Edits an existing product")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = ProductEntity.class),
            @ApiResponse(code = 403, message = "Unauthorized", response = Object.class),
            @ApiResponse(code = 404, message = "Not Found", response = Object.class),
    })
    @PostMapping("/product/{productId}")
    public ProductEntity editProduct(
            @ApiParam(name = "ProductEntity", value = "product", required = true, format = MediaType.MULTIPART_FORM_DATA_VALUE)
            @ModelAttribute ProductEntity product,
            @RequestParam(required = false) MultipartFile file,
            @PathVariable(name = "productId") Long id
    ) {
        product.setProductId(id);
        return productService.editProduct(product, file);
    }

    @ApiOperation(value = "Get Product", notes = "Gets a product with the given ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = ProductEntity.class),
            @ApiResponse(code = 404, message = "Not Found", response = Object.class)
    })
    @GetMapping("/product/{productId}")
    public ProductEntity getProduct(
            @PathVariable Long productId) {

        return productService.getProduct(productId);
    }

    @ApiOperation(value = "Get All Products", notes = "Gets all products", responseContainer = "List", response = ProductEntity.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success")
    })
    @GetMapping("/product")
    public List<ProductEntity> getAllProducts() {
        return productService.getProducts();
    }


    @ApiOperation(value = "Delete a product", notes = "Gets all products", responseContainer = "List", response = ProductEntity.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })
    @DeleteMapping("/product/{productId}")
    public String deleteByProductId(
            @PathVariable Long productId
    ) {
        productService.deleteProductById(productId);
        return "";
    }
}
