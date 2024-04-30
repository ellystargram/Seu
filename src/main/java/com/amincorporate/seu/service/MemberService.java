package com.amincorporate.seu.service;

import com.amincorporate.seu.dto.InfoDTO;
import com.amincorporate.seu.dto.JoinDTO;

public interface MemberService {

    void join(JoinDTO joinDTO);

    void leave(String memberId);

    InfoDTO getInfo(String memberId);

}
