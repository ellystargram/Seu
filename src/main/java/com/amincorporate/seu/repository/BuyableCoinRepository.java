package com.amincorporate.seu.repository;

import com.amincorporate.seu.entity.BuyableCoinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyableCoinRepository extends JpaRepository<BuyableCoinEntity, Long> {
}
