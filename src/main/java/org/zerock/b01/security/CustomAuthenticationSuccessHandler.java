package org.zerock.b01.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;

@Log4j2
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        log.info("로그인 성공: " + authentication.getName());
        
        // URL 파라미터로 전달된 리다이렉트 URL 확인
        String redirectUrl = request.getParameter("redirect");
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            log.info("파라미터로 전달된 URL로 리다이렉트: " + redirectUrl);
            response.sendRedirect(redirectUrl);
            return;
        }
        
        // 저장된 요청이 있는지 확인
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            log.info("이전 요청 URL로 리다이렉트: " + targetUrl);
            
            // 저장된 요청 제거
            requestCache.removeRequest(request, response);
            
            response.sendRedirect(targetUrl);
        } else {
            // 저장된 요청이 없으면 메인 페이지로
            log.info("저장된 요청이 없어 메인 페이지로 리다이렉트");
            response.sendRedirect("/main");
        }
    }
}
