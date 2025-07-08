package org.zerock.b01.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2
public class SampleController {

    @GetMapping("/")
    public String home() {
        log.info("root page requested, redirecting to main...");
        return "redirect:/main"; // 메인 페이지로 리다이렉트
    }
    
    @GetMapping("/main")
    public String main(Model model) {
        log.info("main page requested...");
        model.addAttribute("activeMenu", "main");
        return "main"; // templates 폴더의 main.html을 렌더링
    }
    
    @GetMapping("/explain")
    public String explain(Model model) {
        log.info("explain page requested...");
        model.addAttribute("activeMenu", "explain");
        return "explain"; // templates 폴더의 explain.html을 렌더링
    }
    
    @GetMapping("/intro")
    public String intro(Model model) {
        log.info("intro page requested...");
        model.addAttribute("activeMenu", "intro");
        return "intro"; // templates 폴더의 intro.html을 렌더링
    }
    
    @GetMapping("/history")
    public String history(Model model) {
        log.info("history page requested...");
        model.addAttribute("activeMenu", "history");
        return "history"; // templates 폴더의 history.html을 렌더링
    }
    
    @GetMapping("/map")
    public String map(Model model) {
        log.info("map page requested...");
        model.addAttribute("activeMenu", "map");
        return "map"; // templates 폴더의 map.html을 렌더링
    }
}
