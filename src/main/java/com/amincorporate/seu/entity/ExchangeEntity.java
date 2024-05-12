package com.amincorporate.seu.entity;

import com.amincorporate.seu.entity.wallet.WalletEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Builder
@Table(name = "Exchange")
@AllArgsConstructor
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

    private String tradeType;

    private Double quantity;

    private Double price;

    private Date tradeDate;

}
