package com.amincorporate.seu.configuration;

import com.amincorporate.seu.entity.CoinEntity;
import com.amincorporate.seu.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CoinDataInitializer implements CommandLineRunner {

    private final CoinRepository coinRepository;

    @Override
    public void run(String... args) throws Exception {
        List<CoinEntity> coinEntities = Arrays.asList(
            new CoinEntity("BTC", "Bitcoin", 61027.20, "₿"),
            new CoinEntity("DOGE", "Dogecoin", 0.14, "Ð"),
            new CoinEntity("ETH", "Ethereum", 2911.48, "Ξ"),
            new CoinEntity("SOL", "Solana", 144.56, "◎"),
            new CoinEntity("ADA", "Cardano", 2911.48, "₳")
        );

        coinRepository.saveAll(coinEntities);
    }

}
