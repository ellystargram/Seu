package com.amincorporate.seu.entity.wallet;

import com.amincorporate.seu.entity.MemberEntity;
import com.amincorporate.seu.entity.WalletCoinEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(UniqueStringIdListener.class)
@Table(name = "Wallet")
public class WalletEntity {

    @Id
    @Column(name = "WALLET_ID")
    private String id;

    private WalletType walletType;

    private Date createDate;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private MemberEntity memberEntity;

    @OneToMany(mappedBy = "walletEntity")
    private List<WalletCoinEntity> walletCoinEntities;

    @Builder
    public WalletEntity(WalletType walletType, MemberEntity memberEntity, Date createDate) {
        this.walletType = walletType;
        this.memberEntity = memberEntity;
        this.createDate = createDate;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UniqueStringIdGenerator.generateUniqueString();
        }
    }

}

class UniqueStringIdListener {

    @PrePersist
    public void prePersist(Object object) {
        if (object instanceof WalletEntity walletEntity) {
            walletEntity.generateId();
        }
    }
}

class UniqueStringIdGenerator {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final Set<String> existingIds = new HashSet<>();

    public static String generateUniqueString() {
        while (true) {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                int index = new Random().nextInt(ALPHABET.length());
                sb.append(ALPHABET.charAt(index));
            }
            String generatedString = sb.toString();
            if (existingIds.add(generatedString)) {
                return generatedString;
            }
        }
    }

}