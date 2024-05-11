package com.amincorporate.seu.service;

import com.amincorporate.seu.dto.WalletCreateDTO;
import com.amincorporate.seu.dto.WalletInfoDTO;
import com.amincorporate.seu.entity.wallet.WalletType;

import java.util.List;

public interface WalletService {

    WalletCreateDTO create(String memberId, WalletType walletType);

    List<WalletInfoDTO> getInfo(String memberId);

}
