package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberInitService {
    
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    
    public void initTestUser() {
        // user1 사용자가 없으면 생성
        if (!memberRepository.existsById("user1")) {
            Member member = Member.builder()
                    .mid("user1")
                    .mpw(passwordEncoder.encode("1111"))
                    .email("user1@test.com")
                    .del(false)
                    .social(false)
                    .build();
            
            member.addRole(MemberRole.USER);
            
            memberRepository.save(member);
            log.info("테스트 사용자 user1이 생성되었습니다.");
        }
        
        // admin 사용자가 없으면 생성
        if (!memberRepository.existsById("admin")) {
            Member admin = Member.builder()
                    .mid("admin")
                    .mpw(passwordEncoder.encode("1111"))
                    .email("admin@test.com")
                    .del(false)
                    .social(false)
                    .build();
            
            admin.addRole(MemberRole.USER);
            admin.addRole(MemberRole.ADMIN);
            
            memberRepository.save(admin);
            log.info("관리자 사용자 admin이 생성되었습니다.");
        }
    }
}
