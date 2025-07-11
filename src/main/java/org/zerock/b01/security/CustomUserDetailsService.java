package org.zerock.b01.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Member;
import org.zerock.b01.repository.MemberRepository;
import org.zerock.b01.security.dto.MemberSecurityDTO;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername: " + username);

        Optional<Member> result = memberRepository.getWithRoles(username);

        if(result.isEmpty()){
            throw new UsernameNotFoundException("username not found.......");
        }

        Member member = result.get();
        
        // 탈퇴한 회원인지 확인
        if(member.isDel()){
            throw new UsernameNotFoundException("User account has been deleted.......");
        }

        MemberSecurityDTO memberSecurityDTO =
                new MemberSecurityDTO(
                        member.getMid(),
                        member.getMpw(),
                        member.getEmail(),
                        member.isDel(),
                        false,
                        member.getRegdate(),
                        member.getRoleSet()
                                .stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_"+memberRole.name()))
                                .collect(Collectors.toList())
                );
        log.info("memberSecurityDTO");
        log.info(memberSecurityDTO);

        return memberSecurityDTO;
    }
}
