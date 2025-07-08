package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.dto.MemberJoinDTO;
import org.zerock.b01.security.dto.MemberSecurityDTO;
import org.zerock.b01.service.MemberService;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login")
    public void loginGET(String error, String logout, HttpSession session, Model model) {
        log.info("login get..................");
        log.info("logout : " + logout);

        if(logout != null) {
            log.info("user logout.........");
            session.invalidate();
        }
        model.addAttribute("activeMenu", "login");
    }

    @GetMapping("/join")
    public void joinGET(Model model) {
        log.info("join get...");
        model.addAttribute("activeMenu", "join");
    }

    @PostMapping("/join")
    public String joinPOST(MemberJoinDTO memberJoinDTO, RedirectAttributes redirectAttributes){

        log.info("join post...");
        log.info(memberJoinDTO);

        try{
            memberService.join(memberJoinDTO);
        } catch (MemberService.MidExistException e) {
            redirectAttributes.addFlashAttribute("error", "mid");
            return "redirect:/member/join";
        }

        redirectAttributes.addFlashAttribute("result", "success");

        return "redirect:/member/login";
    }

    @GetMapping("/mypage")
    public void mypageGET(Model model) {
        log.info("mypage get...");
        model.addAttribute("activeMenu", "mypage");
    }

    @GetMapping("/modify")
    public void modifyGET(@AuthenticationPrincipal MemberSecurityDTO memberSecurityDTO, Model model) {
        log.info("modify get...");
        log.info("Current user: " + memberSecurityDTO.getMid());
        
        // 현재 로그인한 사용자 정보를 모델에 추가
        model.addAttribute("member", memberSecurityDTO);
        model.addAttribute("activeMenu", "modify");
    }    @PostMapping("/modify")
    public String modifyPOST(@AuthenticationPrincipal MemberSecurityDTO memberSecurityDTO,
                           String mpw, String email,
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request) {
        log.info("modify post...");
        log.info("User: " + memberSecurityDTO.getMid());
        log.info("New password: " + (mpw != null && !mpw.isEmpty() ? "***" : "no change"));
        log.info("New email: " + email);

        try {
            memberService.modifyMember(memberSecurityDTO.getMid(), mpw, email);
            
            // 비밀번호 변경 시에는 보안상 로그아웃 처리
            if (mpw != null && !mpw.trim().isEmpty()) {
                request.getSession().invalidate();
                redirectAttributes.addFlashAttribute("result", "modified_logout");
                return "redirect:/member/login";
            }
            
            // 이메일만 변경된 경우 세션의 인증 정보 업데이트
            if (email != null && !email.equals(memberSecurityDTO.getEmail())) {
                updateSessionUserInfo(memberSecurityDTO, email);
            }
            
            redirectAttributes.addFlashAttribute("result", "modified");
            return "redirect:/member/mypage";
        } catch (Exception e) {
            log.error("Modify failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "modify_failed");
            return "redirect:/member/modify";
        }
    }
    
    private void updateSessionUserInfo(MemberSecurityDTO currentUser, String newEmail) {
        // 새로운 사용자 정보로 MemberSecurityDTO 생성
        MemberSecurityDTO updatedUser = new MemberSecurityDTO(
                currentUser.getMid(),
                currentUser.getMpw(),
                newEmail,
                currentUser.isDel(),
                currentUser.isSocial(),
                currentUser.getRegdate(),
                currentUser.getAuthorities().stream().toList()
        );
        
        // 새로운 인증 객체 생성
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                updatedUser,
                currentUser.getPassword(),
                updatedUser.getAuthorities()
        );
        
        // SecurityContext 업데이트
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        
        log.info("Session user info updated for: " + currentUser.getMid());
    }

    @GetMapping("/delete")
    public void deleteGET(@AuthenticationPrincipal MemberSecurityDTO memberSecurityDTO, Model model) {
        log.info("delete get...");
        log.info("Current user: " + memberSecurityDTO.getMid());
        
        model.addAttribute("member", memberSecurityDTO);
        model.addAttribute("activeMenu", "delete");
    }    @PostMapping("/delete")
    public String deletePOST(@AuthenticationPrincipal MemberSecurityDTO memberSecurityDTO,
                            String confirmPassword,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        log.info("delete post...");
        log.info("User: " + memberSecurityDTO.getMid());
        
        try {
            // 소셜 로그인 사용자인지 확인
            if (memberSecurityDTO.isSocial()) {
                log.info("Social login user - skipping password verification");
            } else {
                // 일반 사용자는 비밀번호 확인 필수
                if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "password_required");
                    return "redirect:/member/delete";
                }
                
                // 비밀번호 검증
                boolean passwordValid = memberService.verifyPassword(memberSecurityDTO.getMid(), confirmPassword);
                if (!passwordValid) {
                    redirectAttributes.addFlashAttribute("error", "password_mismatch");
                    return "redirect:/member/delete";
                }
            }
            
            // 비밀번호 검증 통과 후 회원탈퇴 진행
            memberService.deleteMember(memberSecurityDTO.getMid());
            
            // 회원탈퇴 후 세션 무효화
            request.getSession().invalidate();
            
            redirectAttributes.addFlashAttribute("result", "deleted");
            return "redirect:/member/login";
        } catch (Exception e) {
            log.error("Delete failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "delete_failed");
            return "redirect:/member/delete";
        }
    }
}
