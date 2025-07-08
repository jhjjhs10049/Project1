package org.zerock.b01.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final PasswordEncoder passwordEncoder;
    private final HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("-------------------------------------------------------------");
        log.info("CustomLoginSuccessHandler onAuthenticationSuccess............");
        log.info("Authentication Principal: {}", authentication.getPrincipal());
        log.info("Authentication Authorities: {}", authentication.getAuthorities());

        try {
            SavedRequest savedRequest = requestCache.getRequest(request, response);

            String redirectUrl = (savedRequest != null)
                    ? savedRequest.getRedirectUrl()
                    : "/";

            log.info("👉 Redirecting to: {}", redirectUrl);
            redirectStrategy.sendRedirect(request, response, redirectUrl);
        } catch (Exception e) {
            log.error("Error during OAuth2 login redirect", e);
            response.sendRedirect("/member/login?error=oauth2_redirect_failed");
        }
    }
}
