package com.amincorporate.seu.dto;

import com.amincorporate.seu.entity.wallet.WalletType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WalletInfoDTO {

    private String id;

    private WalletType walletType;

}
