package com.amincorporate.seu.service;

import com.amincorporate.seu.dto.CreateWalletDTO;
import com.amincorporate.seu.entity.wallet.WalletEntity;
import com.amincorporate.seu.entity.wallet.WalletType;
import com.amincorporate.seu.exception.MemberNoExistsException;
import com.amincorporate.seu.repository.MemberRepository;
import com.amincorporate.seu.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final MemberRepository memberRepository;
    private final WalletRepository walletRepository;

    @Override
    public CreateWalletDTO createWallet(String memberId, WalletType walletType) {
        if (!memberRepository.existsMemberById(memberId)) {
            throw new MemberNoExistsException();
        }

        WalletEntity walletEntity = WalletEntity.builder()
                .memberEntity(memberRepository.findById(memberId).get())
                .walletType(walletType)
                .createDate(new Date())
                .build();

        walletRepository.save(walletEntity);

        CreateWalletDTO createWalletDTO = new CreateWalletDTO();
        createWalletDTO.setWalletId(walletEntity.getId());
        createWalletDTO.setWalletType(walletEntity.getWalletType());

        return createWalletDTO;
    }

}
