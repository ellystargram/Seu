package com.amincorporate.seu.service;

import com.amincorporate.seu.dto.CoinBuyableDTO;
import com.amincorporate.seu.dto.CoinListDTO;
import com.amincorporate.seu.dto.CoinTradeDTO;
import com.amincorporate.seu.entity.CoinEntity;

import java.util.List;

public interface TradeService {

    CoinEntity getCoin(String coinId);

    List<CoinListDTO> getCoinList();

    List<CoinBuyableDTO> getBuyable(String walletId, String inCoinId, Double quantity);

    CoinTradeDTO trade(String walletId, String inCoinId, Double quantity, String outCoinId);

}
