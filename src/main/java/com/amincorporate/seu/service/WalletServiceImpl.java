package com.amincorporate.seu.service;

import com.amincorporate.seu.dto.ExchangeInfoDTO;
import com.amincorporate.seu.dto.WalletCreateDTO;
import com.amincorporate.seu.dto.WalletInfoDTO;
import com.amincorporate.seu.entity.CoinEntity;
import com.amincorporate.seu.entity.ExchangeEntity;
import com.amincorporate.seu.entity.wallet.WalletEntity;
import com.amincorporate.seu.entity.wallet.WalletType;
import com.amincorporate.seu.exception.MemberNoExistsException;
import com.amincorporate.seu.exception.WalletNoExistsException;
import com.amincorporate.seu.repository.CoinRepository;
import com.amincorporate.seu.repository.ExchangeRepository;
import com.amincorporate.seu.repository.MemberRepository;
import com.amincorporate.seu.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final MemberRepository memberRepository;
    private final WalletRepository walletRepository;
    private final CoinRepository coinRepository;
    private final ExchangeRepository exchangeRepository;

    @Override
    public WalletCreateDTO create(String memberId, WalletType walletType) {

        if (!memberRepository.existsMemberById(memberId)) {
            throw new MemberNoExistsException();
        }

        WalletEntity walletEntity = WalletEntity.builder()
                .memberEntity(memberRepository.findById(memberId).get())
                .walletType(walletType)
                .createDate(new Date())
                .build();

        walletRepository.save(walletEntity);

        log.info("월렛 개수: " + String.valueOf(walletRepository.findAllByMemberEntity_Id(memberId).size()));

        if (walletRepository.findAllByMemberEntity_Id(memberId).size() == 1) {
            CoinEntity coinEntity = coinRepository.findById("BTC").get();

            // 초기 자금 1.0 BTC 지급
            exchangeRepository.save(ExchangeEntity.builder()
                    .walletEntity(walletEntity)
                    .coinEntity(coinEntity)
                    .price(coinEntity.getPrice())
                    .quantity(1.0)
                    .tradeType("INITIAL")
                    .tradeDate(new Date())
                    .build());
        }

        WalletCreateDTO walletCreateDTO = new WalletCreateDTO();
        walletCreateDTO.setWalletId(walletEntity.getId());
        walletCreateDTO.setWalletType(walletEntity.getWalletType());

        return walletCreateDTO;
    }

    @Override
    public List<WalletInfoDTO> getInfo(String memberId) {
        List<WalletEntity> walletEntities = walletRepository.findAllByMemberEntity_Id(memberId);

        if (!memberRepository.existsMemberById(memberId)) {
            throw new MemberNoExistsException();
        }

        if (walletEntities.isEmpty()) {
            throw new WalletNoExistsException();
        }

        List<WalletInfoDTO> walletInfoDTOS = new ArrayList<>();

        for (WalletEntity walletEntity : walletEntities) {
            WalletInfoDTO walletInfoDTO = new WalletInfoDTO();
            walletInfoDTO.setId(walletEntity.getId());
            walletInfoDTO.setWalletType(walletEntity.getWalletType());
            walletInfoDTOS.add(walletInfoDTO);
        }

        return walletInfoDTOS;
    }

    @Override
    public List<ExchangeInfoDTO> getInfoDetail(String memberId, String walletId) {

        WalletEntity walletEntity = walletRepository.findByMemberEntity_IdAndId(memberId, walletId).orElseThrow(WalletNoExistsException::new);

        if (!walletEntity.getId().equals(walletId)) {
            throw new WalletNoExistsException();
        }

        Double rate = 1.0;

        switch (walletEntity.getWalletType()) {
            case COPPER -> {
                rate = 1.0;
            }
            case BAUXITE -> {
                rate = 2.0;
            }
            case URANIUM -> {
                rate = 3.0;
            }
        }

        List<ExchangeEntity> exchangeEntities = exchangeRepository.findLatestTradesByCoin(walletId);

        List<ExchangeInfoDTO> exchangeInfoDTOS = new ArrayList<>();

        for (ExchangeEntity exchangeEntity : exchangeEntities) {
            ExchangeInfoDTO exchangeInfoDTO = new ExchangeInfoDTO();
            exchangeInfoDTO.setName(exchangeEntity.getCoinEntity().getName());
            exchangeInfoDTO.setSymbol(exchangeEntity.getCoinEntity().getSymbol());
            exchangeInfoDTO.setPrice(exchangeEntity.getPrice() * rate);
            exchangeInfoDTO.setQuantity(exchangeEntity.getQuantity());
            exchangeInfoDTO.setMaxDecimal(exchangeEntity.getCoinEntity().getMaxDecimal());
            exchangeInfoDTOS.add(exchangeInfoDTO);
        }

        return exchangeInfoDTOS;
    }

    @Override
    public Boolean isWalletExists(String memberId, String walletId) {
        return walletRepository.findByMemberEntity_IdAndId(memberId, walletId).isEmpty();
    }
}
