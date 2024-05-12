package com.amincorporate.seu.repository;

import com.amincorporate.seu.entity.wallet.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, String> {

    List<WalletEntity> findAllByMemberEntity_Id(String memberId);

    Optional<WalletEntity> findByMemberEntity_IdAndId(String memberId, String walletId);

}
