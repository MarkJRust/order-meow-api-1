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

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String PRODUCT_NAME = "Yolo swaggity swag";
    private static final long PRODUCT_ID = 52;

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
        post("/product", objectMapper.writeValueAsString(productEntity)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    void createNewProduct_badRequest() throws Exception {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(PRODUCT_ID);
        productEntity.setProductName(PRODUCT_NAME);

        Mockito.when(productService.createProduct(productEntity)).thenThrow(new ProductExceptions.BadProductName(PRODUCT_NAME));
        post("/product", objectMapper.writeValueAsString(productEntity)).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }


    private ResultActions post(String url, String body) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }
}