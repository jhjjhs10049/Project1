package org.zerock.b01.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.dto.*;
import org.zerock.b01.service.BoardService;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    private final BoardService boardService;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){
//        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);
        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);
        log.info(responseDTO);
        model.addAttribute("responseDTO", responseDTO);        model.addAttribute("activeMenu", "board");
    }    @PreAuthorize("isAuthenticated()")
    @GetMapping("/register")
    public void registerGET(Model model) {
        model.addAttribute("activeMenu", "board");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/register")
    public String registerPost(@Valid BoardDTO boardDTO, BindingResult bindingResult,
                               RedirectAttributes attributes) {
        log.info("board POST register.......");

        if(bindingResult.hasErrors()) {
            log.info("has errors........");
            attributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/register";
        }
        log.info(boardDTO);
        Long bno = boardService.register(boardDTO);
        attributes.addFlashAttribute("bindingResult", bno);        return "redirect:/board/list";
    }    @GetMapping("/read")
    public void read(Long bno, PageRequestDTO pageRequestDTO, Model model, Authentication authentication) {
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info(boardDTO);
        model.addAttribute("dto", boardDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("activeMenu", "board");
        
        // 삭제 권한 체크 로직을 컨트롤러에서 처리
        boolean canDelete = false;
        if (authentication != null && authentication.isAuthenticated()) {
            String currentUser = authentication.getName();
            boolean isWriter = currentUser.equals(boardDTO.getWriter());
            boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            boolean isRole1 = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_1"));
            
            // 작성자이거나 (관리자이면서 ROLE_1이 아닌 경우)
            canDelete = isWriter || (isAdmin && !isRole1);
        }
        model.addAttribute("canDelete", canDelete);
    }@PreAuthorize("isAuthenticated() and (principal.username == #boardDTO.writer)")
    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO, @Valid BoardDTO boardDTO, BindingResult bindingResult, RedirectAttributes attributes) {
        log.info("board modify post......" + boardDTO);
        if(bindingResult.hasErrors()) {
            log.info("has errors........");
            String link = pageRequestDTO.getLink();
            attributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            attributes.addAttribute("bno", boardDTO.getBno());
            return "redirect:/board/modify?" + link;
        }
        boardService.modify(boardDTO);
        attributes.addFlashAttribute("result", "modified");
        attributes.addAttribute("bno", boardDTO.getBno());
        return "redirect:/board/read?" + pageRequestDTO.getLink();
    }@PreAuthorize("isAuthenticated() and (principal.username == #boardDTO.writer)")
    @PostMapping("/remove")
    public String remove(BoardDTO boardDTO, RedirectAttributes redirectAttributes) {
        try {
            Long bno = boardDTO.getBno();
            log.info("remove post.." + bno);

            // 게시물 삭제 전에 첨부파일 정보를 가져와서 삭제
            BoardDTO existingBoard = boardService.readOne(bno);
            
            boardService.remove(bno);

            // 게시물이 데이터베이스 상에서 삭제되었다면 첨부파일 삭제
            if (existingBoard != null && existingBoard.getFileNames() != null && existingBoard.getFileNames().size() > 0) {
                log.info("파일 삭제: " + existingBoard.getFileNames());
                removeFiles(existingBoard.getFileNames());
            }

            redirectAttributes.addFlashAttribute("result", "removed");
            
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "삭제 중 오류가 발생했습니다.");
        }

        return "redirect:/board/list";
    }

    public void removeFiles(List<String> files){
        for (String fileName:files){
            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

            String resourceName = resource.getFilename();

            try{
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();

                //섬네일이 존재한다면
                if (contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    thumbnailFile.delete();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public void modifyGET(Long bno, PageRequestDTO pageRequestDTO, Model model) {
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info("modify GET......" + boardDTO);
        model.addAttribute("dto", boardDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("activeMenu", "board");
    }
}
