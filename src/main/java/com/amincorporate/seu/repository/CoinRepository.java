package com.amincorporate.seu.repository;

import com.amincorporate.seu.entity.CoinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinRepository extends JpaRepository<CoinEntity, String> {
}
