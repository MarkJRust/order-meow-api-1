package com.ordermeow.api.product;

import com.ordermeow.api.CustomGlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("local")
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private static final String PRODUCT_DESCRIPTION = "In the history of products, perhaps ever";
    private static final String PRODUCT_NAME = "This is the best product";
    private static final Long PRODUCT_ID = 59L;
    private static final Principal principal = () -> "i-can-put-whatever-i-want-here";

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new CustomGlobalExceptionHandler())
                .build();
    }

    @Test
    void createNewProduct_success_noImage() throws Exception {
        ProductEntity productEntity = createProductEntity();
        Mockito.when(productService.createProduct(productEntity, null)).thenReturn(productEntity);
        multipartPost("/product", productEntity, null).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    void createNewProduct_success_sendImage() throws Exception {
        ProductEntity productEntity = createProductEntity();
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test contract.pdf",
                        MediaType.APPLICATION_PDF_VALUE,
                        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));

        Mockito.when(productService.createProduct(productEntity, file)).thenReturn(productEntity);
        multipartPost("/product", productEntity, file).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    void createNewProduct_badRequest_badProductNameException() throws Exception {
        ProductEntity productEntity = createProductEntity();
        Mockito.when(productService.createProduct(productEntity, null)).thenThrow(new ProductExceptions.BadProductName(PRODUCT_NAME));
        multipartPost("/product", productEntity, null).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    void createNewProduct_badRequest_badProductDescriptionException() throws Exception {
        ProductEntity productEntity = createProductEntity();
        Mockito.when(productService.createProduct(productEntity, null)).thenThrow(new ProductExceptions.BadProductDescription(PRODUCT_DESCRIPTION));
        multipartPost("/product", productEntity, null).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    void createNewProduct_badRequest_badProductPriceException() throws Exception {
        ProductEntity productEntity = createProductEntity();
        Mockito.when(productService.createProduct(productEntity, null)).thenThrow(new ProductExceptions.BadProductPrice(productEntity.getProductPrice()));
        multipartPost("/product", productEntity, null).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    void createNewProduct_badRequest_InvalidFileException() throws Exception {
        ProductEntity productEntity = createProductEntity();
        Mockito.when(productService.createProduct(productEntity, null)).thenThrow(new ProductExceptions.InvalidFileException());
        multipartPost("/product", productEntity, null).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }


    @Test
    void getProductById_success() throws Exception {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(PRODUCT_ID);
        productEntity.setProductName(PRODUCT_NAME);

        Mockito.when(productService.getProduct(PRODUCT_ID)).thenReturn(productEntity);
        get("/product/" + PRODUCT_ID).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    void getProductById_notFound() throws Exception {
        Mockito.when(productService.getProduct(PRODUCT_ID)).thenThrow(new ProductExceptions.ProductNotFound(PRODUCT_ID));
        get("/product/" + PRODUCT_ID).andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    @Test
    void getAllProducts_success() throws Exception {
        List<ProductEntity> products = new ArrayList<>();

        for (int i = 0; i < Math.random() * 100; i++) {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setProductId((long) i);
            productEntity.setProductName("Product " + i);
            products.add(productEntity);
        }

        Mockito.when(productService.getProducts()).thenReturn(products);
        get("/product").andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    void deleteProductById_success() throws Exception {
        Mockito.doNothing().when(productService).deleteProductById(PRODUCT_ID);
        delete("/product/" + PRODUCT_ID).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    void deleteProductById_notFound() throws Exception {
        Mockito.doThrow(new ProductExceptions.ProductNotFound(PRODUCT_ID))
                .when(productService).deleteProductById(PRODUCT_ID);
        delete("/product/" + PRODUCT_ID).andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    @Test
    void editProduct_notFound() throws Exception {
        ProductEntity productEntity = createProductEntity();
        productEntity.setProductId(PRODUCT_ID);
        Mockito.doThrow(new ProductExceptions.ProductNotFound(PRODUCT_ID))
                .when(productService).editProduct(productEntity, null);
        multipartPost("/product/" + PRODUCT_ID, productEntity, null)
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    @Test
    void editProduct_success() throws Exception {
        ProductEntity productEntity = createProductEntity();
        productEntity.setProductId(PRODUCT_ID);
        Mockito.when(productService.editProduct(productEntity, null)).thenReturn(productEntity);
        multipartPost("/product/" + PRODUCT_ID, productEntity, null)
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    private ResultActions get(String url) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }

    private ResultActions delete(String url) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .delete(url)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }

    private ResultActions multipartPost(String url, ProductEntity productEntity, MockMultipartFile file) throws Exception {
        if (file != null) {
            return mockMvc.perform(MockMvcRequestBuilders
                    .multipart(url)
                    .file(file)
                    .param("productName", productEntity.getProductName())
                    .param("productDescription", productEntity.getProductDescription())
                    .param("productPrice", productEntity.getProductPrice().toPlainString())
                    .principal(principal)
            );
        }

        return mockMvc.perform(MockMvcRequestBuilders
                .multipart(url)
                .param("productName", productEntity.getProductName())
                .param("productDescription", productEntity.getProductDescription())
                .param("productPrice", productEntity.getProductPrice().toPlainString())
                .principal(principal)
        );

    }

    private ProductEntity createProductEntity() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductName(PRODUCT_NAME);
        productEntity.setProductDescription(PRODUCT_DESCRIPTION);
        productEntity.setProductPrice(BigDecimal.ONE);
        return productEntity;
    }
}