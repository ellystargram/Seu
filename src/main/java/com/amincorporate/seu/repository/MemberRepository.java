package com.amincorporate.seu.repository;

import com.amincorporate.seu.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String> {

    Boolean existsMemberById(String memberId);

}
