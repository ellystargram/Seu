package com.amincorporate.seu.service;

import com.amincorporate.seu.dto.CoinBuyableDTO;
import com.amincorporate.seu.dto.CoinListDTO;
import com.amincorporate.seu.dto.CoinTradeDTO;
import com.amincorporate.seu.entity.BuyableCoinEntity;
import com.amincorporate.seu.entity.CoinEntity;
import com.amincorporate.seu.entity.ExchangeEntity;
import com.amincorporate.seu.entity.wallet.WalletEntity;
import com.amincorporate.seu.exception.CoinNoExistsException;
import com.amincorporate.seu.exception.MemberNoExistsException;
import com.amincorporate.seu.exception.MoneyNotEnoughException;
import com.amincorporate.seu.exception.WalletNoExistsException;
import com.amincorporate.seu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final CoinRepository coinRepository;
    private final ExchangeRepository exchangeRepository;
    private final BuyableCoinRepository buyableCoinRepository;
    private final MemberRepository memberRepository;
    private final WalletRepository walletRepository;

    @Override
    public CoinEntity getCoin(String coinId) {
        return coinRepository.findById(coinId).orElseThrow(CoinNoExistsException::new);
    }

    @Override
    public List<CoinListDTO> getCoinList() {

        List<CoinEntity> coinEntities = coinRepository.findAll();

        List<CoinListDTO> coinListDTOS = new ArrayList<>();

        for (CoinEntity coinEntity : coinEntities) {
            CoinListDTO coinListDTO = new CoinListDTO();
            coinListDTO.setId(coinEntity.getId());
            coinListDTO.setName(coinEntity.getName());
            coinListDTO.setPrice(coinEntity.getPrice());
            coinListDTO.setMaxDecimal(coinEntity.getMaxDecimal());
            coinListDTO.setSymbol(coinEntity.getSymbol());
            coinListDTO.setDemand(coinEntity.getDemand());
            coinListDTOS.add(coinListDTO);
        }

        return coinListDTOS;
    }

    @Override
    public List<CoinBuyableDTO> getBuyable(String walletId, String inCoinId, Double quantity) {

        String memberId = walletRepository.findById(walletId).orElseThrow(WalletNoExistsException::new).getMemberEntity().getId();

        if (!memberRepository.existsMemberById(memberId)) {
            throw new MemberNoExistsException();
        } else if (walletRepository.findAllByMemberEntity_Id(memberId).isEmpty()) {
            throw new WalletNoExistsException();
        }

        List<ExchangeEntity> exchangeEntities = exchangeRepository.findLatestTradesByCoin(walletId);

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

    @Override
    public CoinTradeDTO trade(String walletId, String inCoinId, Double quantity, String outCoinId) {

        WalletEntity walletEntity = walletRepository.findById(walletId).orElseThrow(WalletNoExistsException::new);

        CoinEntity inCoin = coinRepository.findById(inCoinId).orElseThrow(CoinNoExistsException::new);
        CoinEntity outCoin = coinRepository.findById(outCoinId).orElseThrow(CoinNoExistsException::new);

        double needUSD = inCoin.getPrice() * quantity;
        List<ExchangeEntity> exchangeEntityList = exchangeRepository.findTopByCoinEntityIdAndWalletEntityIdOrderByTradeDateDesc(outCoinId, walletId);

        if (exchangeEntityList.isEmpty()) {
            throw new MoneyNotEnoughException();
        }

        if (needUSD > exchangeEntityList.getFirst().getPrice() * exchangeEntityList.getFirst().getQuantity()) {
            throw new MoneyNotEnoughException();
        }

        double outCoinQuantity = exchangeEntityList.getFirst().getQuantity() - (needUSD / outCoin.getPrice());

        List<ExchangeEntity> existsExchanges = exchangeRepository.findTopByCoinEntityIdAndWalletEntityIdOrderByTradeDateDesc(inCoinId, walletId);

        ExchangeEntity existsExchange;

        if (existsExchanges.isEmpty()) {
            existsExchange = ExchangeEntity.builder()
                    .price(inCoin.getPrice())
                    .quantity(0.0)
                    .build();
        } else {
            existsExchange = existsExchanges.getFirst();
        }

        CoinTradeDTO coinTradeDTO = new CoinTradeDTO();
        coinTradeDTO.setInCoin(inCoin);
        coinTradeDTO.setOutCoin(outCoin);
        coinTradeDTO.setMaxDecimal(outCoin.getMaxDecimal());

        List<ExchangeEntity> exchangeEntities = new ArrayList<>();

        // inCoin
        exchangeEntities.add(ExchangeEntity.builder()
                .walletEntity(walletEntity)
                .tradeDate(new Date())
                .coinEntity(inCoin)
                .quantity(Double.parseDouble(decimalRound(existsExchange.getQuantity() + quantity, inCoin.getMaxDecimal())))
                .price((existsExchange.getPrice() + inCoin.getPrice()) / 2)
                .build());

        // outCoin
        exchangeEntities.add(ExchangeEntity.builder()
                .walletEntity(walletEntity)
                .tradeDate(new Date())
                .coinEntity(outCoin)
                .quantity(Double.parseDouble(decimalRound(outCoinQuantity, outCoin.getMaxDecimal())))
                .price(outCoin.getPrice())
                .build());

        inCoin.setDemand(quantity);
        coinRepository.save(inCoin);

        outCoin.setDemand(needUSD / outCoin.getPrice() * -1);
        coinRepository.save(outCoin);

        exchangeRepository.saveAll(exchangeEntities);

        return coinTradeDTO;
    }

    private String decimalRound(Double value, int cutDecimal) {
        String decimal = String.valueOf(value).split("\\.")[1];
        int decimalLen = decimal.length();
        if (decimalLen > cutDecimal) decimalLen = cutDecimal;
        return String.format("%." + decimalLen + "f", value);
    }

    private String decimalCeil(Double value, int cutDecimal) {
        String decimal = String.valueOf(value).split("\\.")[1];
        int decimalLen = decimal.length();
        if (decimalLen > cutDecimal) decimalLen = cutDecimal;
        return String.format("%." + decimalLen + "f", Math.ceil(value * Math.pow(10, cutDecimal)) / Math.pow(10, cutDecimal));
    }

    private String decimalFloor(Double value, int cutDecimal) {
        String decimal = String.valueOf(value).split("\\.")[1];
        int decimalLen = decimal.length();
        if (decimalLen > cutDecimal) decimalLen = cutDecimal;
        return String.format("%." + decimalLen + "f", Math.floor(value * Math.pow(10, cutDecimal)) / Math.pow(10, cutDecimal));
    }

}

