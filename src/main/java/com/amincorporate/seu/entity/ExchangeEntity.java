package com.amincorporate.seu.entity;

import com.amincorporate.seu.entity.wallet.WalletEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Table(name = "Exchange")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeEntity {

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

    private Double price;

    private Date tradeDate;

    @Builder
    public ExchangeEntity(WalletEntity walletEntity, CoinEntity coinEntity, String tradeType, Double quantity, Double price, Date tradeDate) {
        this.walletEntity = walletEntity;
        this.coinEntity = coinEntity;
        this.quantity = quantity;
        this.price = price;
        this.tradeDate = tradeDate;
    }
}
