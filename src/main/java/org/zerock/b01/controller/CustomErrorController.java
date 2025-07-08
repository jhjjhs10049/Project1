package org.zerock.b01.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // 에러 상태 코드 가져오기
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        
        log.error("Error occurred - Status: {}, URI: {}, Message: {}, Exception: {}", 
                  status, requestUri, errorMessage, exception);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            // 404 에러는 메인 페이지로 리다이렉트 (단, static resources는 제외)
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                // 정적 리소스나 API 요청은 404 페이지 표시
                if (requestUri != null && (
                    requestUri.startsWith("/api/") || 
                    requestUri.startsWith("/css/") || 
                    requestUri.startsWith("/js/") || 
                    requestUri.startsWith("/images/") ||
                    requestUri.endsWith(".css") ||
                    requestUri.endsWith(".js") ||
                    requestUri.endsWith(".ico") ||
                    requestUri.endsWith(".png") ||
                    requestUri.endsWith(".jpg") ||
                    requestUri.endsWith(".gif")
                )) {
                    model.addAttribute("errorCode", "404");
                    model.addAttribute("errorMessage", "요청한 페이지를 찾을 수 없습니다.");
                    model.addAttribute("requestUri", requestUri);
                    return "error/404";
                }
                
                log.info("404 Error - Redirecting to main page from: {}", requestUri);
                return "redirect:/main";
            }
            
            // 다른 에러들은 해당 에러 페이지로
            switch (statusCode) {
                case 403:
                    model.addAttribute("errorCode", "403");
                    model.addAttribute("errorMessage", "접근이 거부되었습니다.");
                    model.addAttribute("requestUri", requestUri);
                    return "error/403";
                case 500:
                    model.addAttribute("errorCode", "500");
                    model.addAttribute("errorMessage", "서버 내부 오류가 발생했습니다.");
                    model.addAttribute("exception", exception);
                    model.addAttribute("requestUri", requestUri);
                    if (errorMessage != null) {
                        model.addAttribute("detailMessage", errorMessage);
                    }
                    return "error/500";
                default:
                    model.addAttribute("errorCode", String.valueOf(statusCode));
                    model.addAttribute("errorMessage", "예상치 못한 오류가 발생했습니다.");
                    model.addAttribute("requestUri", requestUri);
                    if (errorMessage != null) {
                        model.addAttribute("detailMessage", errorMessage);
                    }
                    return "error/error";
            }
        }
        
        // 상태 코드를 알 수 없는 경우
        model.addAttribute("errorCode", "Unknown");
        model.addAttribute("errorMessage", "알 수 없는 오류가 발생했습니다.");
        model.addAttribute("requestUri", requestUri);
        if (errorMessage != null) {
            model.addAttribute("detailMessage", errorMessage);
        }
        return "error/error";
    }
}
