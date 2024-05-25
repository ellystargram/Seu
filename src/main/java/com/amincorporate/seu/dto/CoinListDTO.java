package com.amincorporate.seu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoinListDTO {

    private String id;

    private String name;

    private Double price;

    private String symbol;

    private int maxDecimal;

    private double demand;

}
