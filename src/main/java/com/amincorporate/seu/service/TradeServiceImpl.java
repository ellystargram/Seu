package com.amincorporate.seu.service;

import com.amincorporate.seu.dto.CoinBuyableDTO;
import com.amincorporate.seu.entity.BuyableCoinEntity;
import com.amincorporate.seu.entity.CoinEntity;
import com.amincorporate.seu.entity.ExchangeEntity;
import com.amincorporate.seu.exception.CoinNoExistsException;
import com.amincorporate.seu.repository.BuyableCoinRepository;
import com.amincorporate.seu.repository.CoinRepository;
import com.amincorporate.seu.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private static final Logger log = LoggerFactory.getLogger(TradeServiceImpl.class);
    private final CoinRepository coinRepository;
    private final ExchangeRepository exchangeRepository;
    private final BuyableCoinRepository buyableCoinRepository;

    @Override
    public CoinEntity getCoin(String coinId) {
        return coinRepository.findById(coinId).orElseThrow(CoinNoExistsException::new);
    }

    @Override
    public List<CoinBuyableDTO> getBuyable(String walletId, String inCoinId, Double quantity) {

        List<ExchangeEntity> exchangeEntities = exchangeRepository.findLatestTradesByCoin();

        if (exchangeEntities.isEmpty()) {
            throw new CoinNoExistsException();
        }

        Map<String, Double> hasCoinIdQuantity = new HashMap<>();

        for (ExchangeEntity exchangeEntity : exchangeEntities) {
            hasCoinIdQuantity.put(exchangeEntity.getCoinEntity().getId(), exchangeEntity.getQuantity());
        }

        CoinEntity coinEntity = coinRepository.findById(inCoinId).get();

        List<BuyableCoinEntity> buyableCoinEntities = buyableCoinRepository.findAllByBaseCoinEntity(coinEntity);

        List<CoinBuyableDTO> coinBuyableDTOS = new ArrayList<>();

        for (BuyableCoinEntity buyableCoinEntity : buyableCoinEntities) {
            String buyableCoinId = buyableCoinEntity.getBuyableCoinEntity().getId();
            Double quantityForCoin = hasCoinIdQuantity.get(buyableCoinId);

            Double targetUSD = coinRepository.findById(buyableCoinId).get().getPrice();
            Double needUSD = coinEntity.getPrice() * quantity;

            if (quantityForCoin != null && targetUSD * quantityForCoin >= needUSD) {
                CoinBuyableDTO coinBuyableDTO = new CoinBuyableDTO();
                coinBuyableDTO.setCoinEntity(buyableCoinEntity.getBuyableCoinEntity());
                coinBuyableDTOS.add(coinBuyableDTO);
            }
        }

        return coinBuyableDTOS;
    }
}
