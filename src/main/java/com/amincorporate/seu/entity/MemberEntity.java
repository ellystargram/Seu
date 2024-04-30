package com.amincorporate.seu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity {

    @Id
    @Column(name = "MEMBER_ID")
    private String id;

    private String name;

    private Date discordJoinDate;

    private Date seuJoinDate;

    @OneToMany
    @JoinColumn(name = "WALLET_ID")
    private List<WalletEntity> walletEntity;

}
