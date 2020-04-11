package com.ordermeow.api.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "ordered_products")
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The id of the row")
    private Long id;

    @Column
    @ApiModelProperty(notes = "The Order UUID")
    private String orderUuid;

    @Column
    @ApiModelProperty(notes = "The product's name")
    @NotNull
    private String productName;

    @Column
    @ApiModelProperty(notes = "The product's price")
    @NotNull
    private BigDecimal productPrice;

    @Column
    @ApiModelProperty(notes = "The order total")
    private BigDecimal total;

}
