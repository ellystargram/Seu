package com.amincorporate.seu.dto;

import com.amincorporate.seu.entity.CoinEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoinTradeDTO {

    private CoinEntity inCoin;

    private CoinEntity outCoin;

    private int maxDecimal;

}
