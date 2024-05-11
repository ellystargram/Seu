package com.amincorporate.seu.entity;

import com.amincorporate.seu.entity.wallet.WalletEntity;
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

    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalletEntity> walletEntity;

}
