package com.ordermeow.api.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.istack.Nullable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The id of the product")
    @Nullable
    private Long productId;

    @Column
    @ApiModelProperty(notes = "The product's name")
    @NotNull
    private String productName;

    @Column
    @ApiModelProperty(notes = "The product's description")
    @NotNull
    private String productDescription;

    @Column
    @ApiModelProperty(notes = "The product's price")
    @NotNull
    private BigDecimal productPrice;

    @Column
    @ApiModelProperty(notes = "The product's image")
    @Nullable
    @Lob
    private byte[] productImage;
}
