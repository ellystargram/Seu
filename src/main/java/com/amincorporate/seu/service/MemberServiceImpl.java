package com.amincorporate.seu.service;

import com.amincorporate.seu.dto.MemberInfoDTO;
import com.amincorporate.seu.dto.MemberJoinDTO;
import com.amincorporate.seu.entity.MemberEntity;
import com.amincorporate.seu.exception.MemberExistsException;
import com.amincorporate.seu.exception.MemberNoExistsException;
import com.amincorporate.seu.repository.MemberRepository;
import com.amincorporate.seu.work.MemberMessageWork;
import com.amincorporate.seu.work.WalletMessageWork;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

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
    public void leave(String memberId) {
        if (!memberRepository.existsMemberById(memberId)) {
            throw new MemberNoExistsException();
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
