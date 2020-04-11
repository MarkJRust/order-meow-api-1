package com.ordermeow.api.order;

import com.ordermeow.api.product.ProductEntity;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping()
@Api(tags = {"Payment Calculator and Processor API"})
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @ApiOperation(value = "Calculate Total", notes = "Calculates the total of the products passed in as a List")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = ProductEntity.class),
            @ApiResponse(code = 404, message = "Product Not Found", response = Object.class)
    })
    @PostMapping("/calculate")
    public BigDecimal calculateTotal(
            @RequestBody List<Long> productIds) {
        return orderService.calculateTotal(productIds);
    }

    @ApiOperation(value = "Order", notes = "Generate an order number and save it in the database", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = OrderEntity.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Order Not Found", response = Object.class)
    })
    @PostMapping("/order")
    public List<OrderEntity> createOrder(
//            @RequestPart List<Long> productIds,
//            @RequestPart BigDecimal expectedCost
            @ApiParam(example = "{ garbage: garbage }") @RequestBody Map<String, ?> order


    ) {
        List<Integer> productIdsInt = (List<Integer>) order.get("productIds");
        List<Long> productIds = new ArrayList<>();
        productIdsInt.stream().mapToLong(i -> (long) i).forEach(productIds::add);

        if (productIds.size() == 0) {
            throw new RuntimeException("No products specified");
        }

        Object costObj = order.get("cost");
        BigDecimal expectedCost;

        if (costObj instanceof String) {
            expectedCost = new BigDecimal((String) costObj);
        } else if (costObj instanceof Integer) {
            expectedCost = new BigDecimal((Integer) costObj);
        } else if (costObj instanceof Double) {
            expectedCost = BigDecimal.valueOf((Double) costObj);
        } else {
            throw new RuntimeException("Unsupported cost type");
        }

        expectedCost = expectedCost.setScale(2, RoundingMode.CEILING);

        return orderService.createOrder(productIds, expectedCost);
    }


}
