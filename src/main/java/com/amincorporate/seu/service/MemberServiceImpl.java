package com.amincorporate.seu.service;

import com.amincorporate.seu.dto.MemberInfoDTO;
import com.amincorporate.seu.dto.MemberJoinDTO;
import com.amincorporate.seu.entity.MemberEntity;
import com.amincorporate.seu.entity.wallet.WalletEntity;
import com.amincorporate.seu.exception.MemberExistsException;
import com.amincorporate.seu.exception.MemberNoExistsException;
import com.amincorporate.seu.repository.ExchangeRepository;
import com.amincorporate.seu.repository.MemberRepository;
import com.amincorporate.seu.repository.WalletRepository;
import com.amincorporate.seu.work.MemberMessageWork;
import com.amincorporate.seu.work.WalletMessageWork;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ExchangeRepository exchangeRepository;
    private final WalletRepository walletRepository;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일");

    @Override
    public void join(MemberJoinDTO memberJoinDTO) {
        if (memberRepository.existsMemberById(memberJoinDTO.getId())) {
            throw new MemberExistsException();
        }

        memberRepository.save(MemberEntity.builder()
                .id(memberJoinDTO.getId())
                .name(memberJoinDTO.getName())
                .discordJoinDate(memberJoinDTO.getDiscordJoinDate())
                .seuJoinDate(new Date())
                .build());
    }

    @Override
    @Transactional
    public void leave(String memberId) {
        if (!memberRepository.existsMemberById(memberId)) {
            throw new MemberNoExistsException();
        }

        MemberEntity memberEntity = memberRepository.findById(memberId).get();

        for (WalletEntity walletEntity : memberEntity.getWalletEntity()) {
            exchangeRepository.deleteAllByWalletEntityId(walletEntity.getId());
            walletRepository.deleteById(walletEntity.getId());
        }

        memberRepository.deleteById(memberId);
    }

    @Override
    public MemberInfoDTO getInfo(String memberId) {
        if (!memberRepository.existsMemberById(memberId)) {
            throw new MemberNoExistsException();
        }

        MemberEntity memberEntity = memberRepository.findById(memberId).get();

        MemberInfoDTO memberInfoDTO = new MemberInfoDTO();
        memberInfoDTO.setName(memberEntity.getName());
        memberInfoDTO.setDiscordJoinDate(formatter.format(memberEntity.getDiscordJoinDate()));
        memberInfoDTO.setSeuJoinDate(formatter.format(memberEntity.getSeuJoinDate()));

        return memberInfoDTO;
    }

    @Override
    public Boolean isMemberExists(String memberId) {
        return memberRepository.existsMemberById(memberId);
    }
}
