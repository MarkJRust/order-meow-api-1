package com.ordermeow.api.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordermeow.api.CustomGlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("local")
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private static final String PRODUCT_NAME = "Yolo swaggity swag";
    private static final long PRODUCT_ID = 52;
    @Mock
    private ProductService productService;
    @InjectMocks
    private ProductController productController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new CustomGlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createNewProduct_success() throws Exception {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(PRODUCT_ID);
        productEntity.setProductName(PRODUCT_NAME);

        Mockito.when(productService.createProduct(productEntity)).thenReturn(productEntity);
        post(objectMapper.writeValueAsString(productEntity)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    void createNewProduct_badRequest() throws Exception {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(PRODUCT_ID);
        productEntity.setProductName(PRODUCT_NAME);

        Mockito.when(productService.createProduct(productEntity)).thenThrow(new ProductExceptions.BadProductName(PRODUCT_NAME));
        post(objectMapper.writeValueAsString(productEntity)).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
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

        //TODO - I didn't write get all products yet so gotta do this test after that
        for (int i = 0; i < Math.random() * 100; i++) {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setProductId((long) i);
            productEntity.setProductName("Product " + i);
            products.add(productEntity);
        }
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


    private ResultActions post(String body) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post("/product")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
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
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }
}