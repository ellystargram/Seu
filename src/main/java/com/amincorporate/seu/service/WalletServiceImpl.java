package com.amincorporate.seu.service;

import com.amincorporate.seu.entity.WalletEntity;
import com.amincorporate.seu.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public void createWallet(String userId, String walletName) {



        WalletEntity.builder().build();
    }

}
