package com.amincorporate.seu.entity;

import com.amincorporate.seu.entity.wallet.WalletEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "WalletCoin")
public class WalletCoinEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "WALLET_ID")
    private WalletEntity walletEntity;

    @ManyToOne
    @JoinColumn(name = "COIN_ID")
    private CoinEntity coinEntity;

    private Double quantity;

}
