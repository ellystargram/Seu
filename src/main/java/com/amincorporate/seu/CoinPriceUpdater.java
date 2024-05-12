package com.amincorporate.seu;

import com.amincorporate.seu.entity.CoinEntity;
import com.amincorporate.seu.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CoinPriceUpdater {

    private final CoinRepository coinRepository;

    //todo fixedRate 코인 가격 변동 주기 구성
    @Scheduled(fixedRate = 300000) // ms
    public void updateCoinPrices() {
        List<CoinEntity> coinEntities = coinRepository.findAll();

        for (CoinEntity coinEntity : coinEntities) {
            //todo 가격 변동 로직 구성
            //coinEntity.setPrice();
        }

        coinRepository.saveAll(coinEntities);
    }
}
