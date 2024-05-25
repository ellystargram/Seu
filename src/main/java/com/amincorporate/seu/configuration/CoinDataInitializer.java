package com.amincorporate.seu.configuration;

import com.amincorporate.seu.entity.BuyableCoinEntity;
import com.amincorporate.seu.entity.CoinEntity;
import com.amincorporate.seu.repository.BuyableCoinRepository;
import com.amincorporate.seu.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CoinDataInitializer implements CommandLineRunner {

    Random rand = new Random();

    private final CoinRepository coinRepository;

    @Override
    public void run(String... args) throws Exception {

        List<CoinEntity> coinEntities = Arrays.asList(
                new CoinEntity("BTC", "Bitcoin", 61027.20, "₿", 8, 0),
                new CoinEntity("ETH", "Ethereum", 2911.48, "Ξ", 6, 0),
                new CoinEntity("SOL", "Solana", 144.56, "◎", 2, 0),
                new CoinEntity("ADA", "Cardano", 0.44, "₳", 1, 0),
                new CoinEntity("DOGE", "Dogecoin", 0.14, "Ð", 0, 0)
        );

        coinRepository.saveAll(coinEntities);
    }



}
