package com.amincorporate.seu.configuration;

import com.amincorporate.seu.entity.BuyableCoinEntity;
import com.amincorporate.seu.entity.CoinEntity;
import com.amincorporate.seu.repository.BuyableCoinRepository;
import com.amincorporate.seu.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CoinDataInitializer implements CommandLineRunner {

    Random rand = new Random();

    private static final Logger log = LoggerFactory.getLogger(CoinDataInitializer.class);
    private final CoinRepository coinRepository;
    private final BuyableCoinRepository buyableCoinRepository;

    @Override
    public void run(String... args) throws Exception {
        List<CoinEntity> coinEntities = Arrays.asList(
                new CoinEntity("BTC", "Bitcoin", 61027.20, "₿"),
                new CoinEntity("ETH", "Ethereum", 2911.48, "Ξ"),
                new CoinEntity("SOL", "Solana", 144.56, "◎"),
                new CoinEntity("ADA", "Cardano", 0.44, "₳"),
                new CoinEntity("DOGE", "Dogecoin", 0.14, "Ð")
        );

        List<BuyableCoinEntity> buyableCoinEntities = new ArrayList<>();

        refreshBuyableList(coinEntities, buyableCoinEntities);

        coinRepository.saveAll(coinEntities);
        buyableCoinRepository.saveAll(buyableCoinEntities);
    }

    private void refreshBuyableList(List<CoinEntity> coinEntities, List<BuyableCoinEntity> buyableCoinEntities) {

        buyableCoinEntities.clear();

        for (int i = 0; i < coinEntities.size(); i++) {
            int countBuyable = rand.nextInt(coinEntities.size() - 1) + 1;

            Set<Integer> generated = new HashSet<>();
            while (generated.size() < countBuyable) {
                int rndNum = rand.nextInt(coinEntities.size());

                if (rndNum == i) {
                    continue;
                }

                generated.add(rndNum);
            }

            for (int j : generated) {
                buyableCoinEntities.add(new BuyableCoinEntity(coinEntities.get(i), coinEntities.get(j)));
            }

        }

    }

}
