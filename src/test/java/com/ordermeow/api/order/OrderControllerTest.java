package com.ordermeow.api.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordermeow.api.CustomGlobalExceptionHandler;
import com.ordermeow.api.product.ProductExceptions;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@ActiveProfiles("local")
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {


    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new CustomGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void calculateTotal_success() throws Exception {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);

        BigDecimal total = BigDecimal.ONE;
        Mockito.when(orderService.calculateTotal(ids)).thenReturn(total);
        post(objectMapper.writeValueAsString(ids))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    void calculateTotal_productNotFound() throws Exception {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);

        Mockito.when(orderService.calculateTotal(ids)).thenThrow(new ProductExceptions.ProductNotFound(1L));
        post(objectMapper.writeValueAsString(ids))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }


    private ResultActions post(String ids) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post("/calculate")
                .content(ids)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }
}