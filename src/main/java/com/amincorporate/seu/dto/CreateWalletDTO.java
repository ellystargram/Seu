package com.amincorporate.seu.dto;

import com.amincorporate.seu.entity.wallet.WalletType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWalletDTO {

    private String walletId;

    private WalletType walletType;

}
