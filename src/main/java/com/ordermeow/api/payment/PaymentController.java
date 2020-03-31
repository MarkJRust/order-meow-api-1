package com.ordermeow.api.payment;

import com.ordermeow.api.product.ProductEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping()
@Api(tags = {"Payment Calculator and Processor API"})
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @ApiOperation(value = "Calculate Total", notes = "Calculates the total of the products passed in as a List")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = ProductEntity.class),
            @ApiResponse(code = 404, message = "Product Not Found", response = Object.class)
    })
    @PostMapping("/calculate")
    public BigDecimal calculateTotal(
            @RequestBody List<Long> productIds) {
        return paymentService.calculateTotal(productIds);
    }


}
