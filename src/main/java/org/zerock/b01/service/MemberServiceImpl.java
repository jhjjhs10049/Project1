package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.dto.MemberJoinDTO;
import org.zerock.b01.repository.MemberRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;    @Override
    public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {
        String mid = memberJoinDTO.getMid();

        boolean exist = memberRepository.existsById(mid);

        if(exist){
            throw new MidExistException();
        }

        Member member = Member.builder()
                .mid(memberJoinDTO.getMid())
                .mpw(passwordEncoder.encode(memberJoinDTO.getMpw()))
                .email(memberJoinDTO.getEmail())
                .del(false)
                .social(false)
                .build();
        
        // 기본 사용자 역할 추가
        member.addRole(MemberRole.USER);

        log.info("===============================");
        log.info(member);
        log.info(member.getRoleSet());

        memberRepository.save(member);
    }

    @Override
    public void modifyMember(String mid, String mpw, String email) throws Exception {
        log.info("modifyMember: " + mid);
        
        // 회원 존재 여부 확인
        if (!memberRepository.existsById(mid)) {
            throw new Exception("회원이 존재하지 않습니다.");
        }
        
        // 회원 정보 조회
        Member member = memberRepository.findById(mid).orElseThrow();
        
        // 비밀번호 변경 (비어있지 않은 경우에만)
        if (mpw != null && !mpw.trim().isEmpty()) {
            member.changePassword(passwordEncoder.encode(mpw));
            log.info("Password updated for member: " + mid);
        }
        
        // 이메일 변경
        if (email != null && !email.trim().isEmpty()) {
            member.changeEmail(email);
            log.info("Email updated for member: " + mid + " to " + email);
        }
        
        memberRepository.save(member);
        log.info("Member information updated successfully: " + mid);
    }

    @Override
    public void deleteMember(String mid) throws Exception {
        log.info("deleteMember: " + mid);
        
        // 회원 존재 여부 확인
        if (!memberRepository.existsById(mid)) {
            throw new Exception("회원이 존재하지 않습니다.");
        }
        
        // 회원 정보 조회
        Member member = memberRepository.findById(mid).orElseThrow();
        
        // 실제 삭제 대신 del 플래그를 true로 설정 (논리 삭제)
        member.changeDel(true);
        
        memberRepository.save(member);
        log.info("Member marked as deleted successfully: " + mid);
    }

    @Override
    public boolean verifyPassword(String mid, String rawPassword) throws Exception {
        log.info("verifyPassword: " + mid);
        
        // 회원 존재 여부 확인
        if (!memberRepository.existsById(mid)) {
            throw new Exception("회원이 존재하지 않습니다.");
        }
        
        // 회원 정보 조회
        Member member = memberRepository.findById(mid).orElseThrow();
        
        // 탈퇴한 회원인지 확인
        if (member.isDel()) {
            throw new Exception("탈퇴한 회원입니다.");
        }
        
        // 비밀번호 검증
        boolean matches = passwordEncoder.matches(rawPassword, member.getMpw());
        log.info("Password verification result for " + mid + ": " + matches);
        
        return matches;
    }
}
