package com.amincorporate.seu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Coin")
public class CoinEntity {

    @Id
    @Column(name = "COIN_ID")
    private String id;

    private String name;

    private Double price;

    private String symbol;

    private int maxDecimal;

    private double demand;

}
