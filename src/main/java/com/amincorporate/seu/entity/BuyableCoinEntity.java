package com.amincorporate.seu.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "BuyableCoin")
public class BuyableCoinEntity {

    @Id
    private Long baseCoinId;

    @ManyToOne
    @JoinColumn(name = "BUYABLE_COIN_ID")
    private CoinEntity coinEntity;

}
