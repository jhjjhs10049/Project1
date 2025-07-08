package org.zerock.b01.service;

import org.zerock.b01.dto.MemberJoinDTO;

public interface MemberService{
    static class MidExistException extends Exception{

    }
      void join(MemberJoinDTO memberJoinDTO) throws MidExistException;
    
    void modifyMember(String mid, String mpw, String email) throws Exception;
    
    void deleteMember(String mid) throws Exception;
    
    boolean verifyPassword(String mid, String rawPassword) throws Exception;
}
