package com.amincorporate.seu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(UniqueStringIdListener.class)
public class WalletEntity {

    @Id
    @Column(name = "WALLET_ID")
    private String id;

    private WalletType walletType;

    @Builder
    public WalletEntity(WalletType walletType) {
        this.walletType = walletType;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UniqueStringIdGenerator.generateUniqueString();
        }
    }

}

@Getter
@Setter
@Embeddable
class WalletType {

    public String name;

    public Double rate;

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