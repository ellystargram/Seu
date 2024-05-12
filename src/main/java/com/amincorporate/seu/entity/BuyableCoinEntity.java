package com.amincorporate.seu.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "BuyableCoin")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BuyableCoinEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "BASE_COIN_ID")
    private CoinEntity baseCoinEntity;

    @ManyToOne
    @JoinColumn(name = "BUYABLE_COIN_ID")
    private CoinEntity buyableCoinEntity;

    public BuyableCoinEntity(CoinEntity baseCoinEntity, CoinEntity buyableCoinEntity) {
        this.baseCoinEntity = baseCoinEntity;
        this.buyableCoinEntity = buyableCoinEntity;
    }
}
