package com.amincorporate.seu.repository;

import com.amincorporate.seu.entity.CoinEntity;
import com.amincorporate.seu.entity.wallet.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoinRepository extends JpaRepository<CoinEntity, String> {

}
