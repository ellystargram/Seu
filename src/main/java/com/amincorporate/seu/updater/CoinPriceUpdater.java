package com.amincorporate.seu.updater;

import com.amincorporate.seu.entity.CoinEntity;
import com.amincorporate.seu.entity.MemberEntity;
import com.amincorporate.seu.repository.CoinRepository;
import com.amincorporate.seu.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoinPriceUpdater {

    private final MemberRepository memberRepository;
    private final CoinRepository coinRepository;

    //todo fixedRate 코인 가격 변동 주기 구성
    @Scheduled(fixedRate = 60000) // ms
    public void updateCoinPrices() {
        List<CoinEntity> coinEntities = coinRepository.findAll();
        List<MemberEntity> memberEntities = memberRepository.findAll();

        List<Double> doubles = calculateNewPercentage(coinEntities, memberEntities.size());
        Random random = new Random();

        for (CoinEntity coinEntity : coinEntities) {
            coinEntity.setDemand(0);
            double randomChange = random.nextDouble(-0.05, 0.05);
            coinEntity.setPrice(Double.valueOf(decimalRound(coinEntity.getPrice() * (1 + doubles.get(coinEntities.indexOf(coinEntity)) + randomChange), 2)));
        }

        coinRepository.saveAll(coinEntities);
        log.info("<---- CoinPriceUpdated ---->");
    }

    private List<Double> calculateNewPercentage(List<CoinEntity> coinEntities, int users) {

        double sum = 0;

        for (CoinEntity coinEntity : coinEntities) {
            sum += Math.abs(coinEntity.getDemand()) * coinEntity.getPrice();
        }

        double avg = sum / coinEntities.size();

        double minChange = 0;
        double maxChange = 0;

        double targetMaxChangePercentage = 0.07;
        double targetMinChangePercentage = -0.07;

        for (CoinEntity coinEntity : coinEntities) {
            if (coinEntity.getPrice() * coinEntity.getDemand() / avg < minChange) {
                minChange = coinEntity.getPrice() * coinEntity.getDemand() / avg;
            }

            if (coinEntity.getPrice() * coinEntity.getDemand() / avg > maxChange) {
                maxChange = coinEntity.getPrice() * coinEntity.getDemand() / avg;
            }
        }

        double targetMax = maxChange / users;
        double targetMin = minChange / users;

        if (targetMax > targetMaxChangePercentage) targetMax = targetMaxChangePercentage;
        if (targetMin < targetMinChangePercentage) targetMin = targetMinChangePercentage;

        List<Double> changedPercentages = new ArrayList<>();

        for (CoinEntity coinEntity : coinEntities) {
            changedPercentages.add(map(coinEntity.getPrice() * coinEntity.getDemand() / avg, minChange, maxChange, targetMin, targetMax));
        }

        return changedPercentages;

    }

    private double map(double value, double start1, double stop1, double start2, double stop2) {
        if (stop1 == start1) return 0;
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

    private String decimalRound(Double value, int cutDecimal) {
        String decimal = String.valueOf(value).split("\\.")[1];
        int decimalLen = decimal.length();
        if (decimalLen > cutDecimal) decimalLen = cutDecimal;
        return String.format("%." + decimalLen + "f", value);
    }

}
