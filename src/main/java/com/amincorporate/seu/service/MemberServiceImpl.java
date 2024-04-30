package com.amincorporate.seu.service;

import com.amincorporate.seu.dto.InfoDTO;
import com.amincorporate.seu.dto.JoinDTO;
import com.amincorporate.seu.entity.MemberEntity;
import com.amincorporate.seu.exception.MemberExistsException;
import com.amincorporate.seu.exception.MemberNoExistsException;
import com.amincorporate.seu.repository.MemberRepository;
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
    public void join(JoinDTO joinDTO) {
        if (memberRepository.existsMemberById(joinDTO.getId())) {
            throw new MemberExistsException();
        }

        memberRepository.save(MemberEntity.builder()
                .id(joinDTO.getId())
                .name(joinDTO.getName())
                .discordJoinDate(joinDTO.getDiscordJoinDate())
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
    public InfoDTO getInfo(String memberId) {
        if (!memberRepository.existsMemberById(memberId)) {
            throw new MemberNoExistsException();
        }

        MemberEntity memberEntity = memberRepository.findById(memberId).get();

        InfoDTO infoDTO = new InfoDTO();
        infoDTO.setName(memberEntity.getName());
        infoDTO.setDiscordJoinDate(formatter.format(memberEntity.getDiscordJoinDate()));
        infoDTO.setSeuJoinDate(formatter.format(memberEntity.getSeuJoinDate()));

        return infoDTO;
    }

}
