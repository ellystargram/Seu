package com.amincorporate.seu.updater;

import com.amincorporate.seu.entity.BuyableCoinEntity;
import com.amincorporate.seu.entity.CoinEntity;
import com.amincorporate.seu.repository.BuyableCoinRepository;
import com.amincorporate.seu.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuyableListUpdater {

    Random rand = new Random();

    private final CoinRepository coinRepository;
    private final BuyableCoinRepository buyableCoinRepository;

    @Scheduled(fixedRate = 60000)
    public void updateBuyableList() {

        buyableCoinRepository.deleteAll();

        List<CoinEntity> coinEntities;

        do {
            coinEntities = coinRepository.findAll();
        } while (coinEntities.isEmpty());

        List<BuyableCoinEntity> newBuyableCoinEntities = refreshBuyableList(coinEntities, new ArrayList<>());

        buyableCoinRepository.saveAll(newBuyableCoinEntities);
        log.info("<---- BuyableListUpdated ---->");
    }

    private List<BuyableCoinEntity> refreshBuyableList(List<CoinEntity> coinEntities, List<BuyableCoinEntity> buyableCoinEntities) {

        for (int i = 0; i < coinEntities.size(); i++) {
            int countBuyable = rand.nextInt(coinEntities.size() - 1) + 1;

            Set<Integer> generated = new HashSet<>();
            while (generated.size() < countBuyable) {
                int rndNum = rand.nextInt(coinEntities.size());

                if (rndNum == i) continue;

                generated.add(rndNum);
            }

            for (int j : generated) {
                buyableCoinEntities.add(new BuyableCoinEntity(coinEntities.get(i), coinEntities.get(j)));
            }

        }

        return buyableCoinEntities;

    }
}
