package com.amincorporate.seu.service;

import com.amincorporate.seu.dto.CreateWalletDTO;
import com.amincorporate.seu.entity.wallet.WalletType;

public interface WalletService {

    CreateWalletDTO createWallet(String memberId, WalletType walletType);

}
