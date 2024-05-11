package com.amincorporate.seu.service;

import com.amincorporate.seu.dto.MemberInfoDTO;
import com.amincorporate.seu.dto.MemberJoinDTO;

public interface MemberService {

    void join(MemberJoinDTO memberJoinDTO);

    void leave(String memberId);

    MemberInfoDTO getInfo(String memberId);

    Boolean isMemberExists(String memberId);

}
