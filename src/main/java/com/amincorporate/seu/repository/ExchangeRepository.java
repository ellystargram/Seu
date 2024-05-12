package com.amincorporate.seu.repository;

import com.amincorporate.seu.entity.ExchangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeEntity, Long> {

    @Query(value = "SELECT * FROM (SELECT *, ROW_NUMBER() OVER(PARTITION BY coin_id ORDER BY trade_date DESC) AS rn FROM exchange) AS subquery WHERE rn = 1", nativeQuery = true)
    List<ExchangeEntity> findLatestTradesByCoin();

}
