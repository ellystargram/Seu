package com.amincorporate.seu.repository;

import com.amincorporate.seu.entity.ExchangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeEntity, Long> {

    @Query(value = "SELECT * FROM (SELECT *, ROW_NUMBER() OVER(PARTITION BY coin_id ORDER BY trade_date DESC) AS rn FROM exchange WHERE wallet_id = :walletId) AS subquery WHERE rn = 1", nativeQuery = true)
    List<ExchangeEntity> findLatestTradesByCoin(@Param("walletId") String walletId);


    void deleteAllByWalletEntityId(String walletId);

    List<ExchangeEntity> findTopByCoinEntityIdAndWalletEntityIdOrderByTradeDateDesc(String coinId, String walletId);

}
