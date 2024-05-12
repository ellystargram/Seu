package com.amincorporate.seu.repository;

import com.amincorporate.seu.entity.WalletCoinEntity;
import com.amincorporate.seu.entity.wallet.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletCoinRepository extends JpaRepository<WalletCoinEntity, Long> {

}
