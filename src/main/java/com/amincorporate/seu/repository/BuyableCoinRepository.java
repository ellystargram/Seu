package com.amincorporate.seu.repository;

import com.amincorporate.seu.entity.BuyableCoinEntity;
import com.amincorporate.seu.entity.CoinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyableCoinRepository extends JpaRepository<BuyableCoinEntity, Long> {

    List<BuyableCoinEntity> findAllByBaseCoinEntity(CoinEntity baseCoinEntity);
}
