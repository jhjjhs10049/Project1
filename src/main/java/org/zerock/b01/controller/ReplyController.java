package org.zerock.b01.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReplyDTO;
import org.zerock.b01.service.ReplyService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
@Log4j2
public class ReplyController {
    private final ReplyService replyService;    @Operation(description = "Replies POST", summary = "POST 방식으로 댓글 등록")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
//  PostMapping 의 consumes 속성 : 메소드에서 소비하는 데이터 종류 명시 가능.
    public Map<String, Long> register(@Valid @RequestBody ReplyDTO replyDTO,
                                      BindingResult bindingResult) throws BindException {
        log.info("댓글 등록 요청 - ReplyDTO: {}", replyDTO);
        
        if(bindingResult.hasErrors()) {
            log.error("유효성 검사 실패: {}", bindingResult.getAllErrors());
            throw new BindException(bindingResult);
        }
        
        try {
            Map<String, Long> resultMap = new HashMap<>();
            Long rno = replyService.register(replyDTO);
            resultMap.put("rno", rno);
            log.info("댓글 등록 성공 - RNO: {}", rno);
            return resultMap;
        } catch (Exception e) {
            log.error("댓글 등록 중 오류 발생", e);
            throw e;
        }
    }

    @Operation(description = "Replies of Board", summary = "GET 방식으로 특정 게시물의 댓글 목록을 불러옴.")
    @GetMapping(value = "/list/{bno}")
    public PageResponseDTO<ReplyDTO> getList(@PathVariable("bno") Long bno, PageRequestDTO pageRequestDTO) {
        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard(bno, pageRequestDTO);
        return responseDTO;
    }

    @Operation(description = "Read Reply", summary = "GET 방식으로 특정 댓글 조회")
    @GetMapping(value = "/{rno}")
    public ReplyDTO getReplyDTO(@PathVariable("rno") Long rno) {
        ReplyDTO replyDTO = replyService.read(rno);
        return replyDTO;
    }    @Operation(description = "Delete Reply", summary = "DELETE 방식으로 특정 댓글 삭제")
    @DeleteMapping(value = "/{rno}")
    public Map<String, Long> remove(@PathVariable("rno") Long rno) {
        log.info("댓글 삭제 요청 - RNO: {}", rno);
        
        try {
            replyService.remove(rno);
            Map<String, Long> resultMap = new HashMap<>();
            resultMap.put("rno", rno);
            log.info("댓글 삭제 성공 - RNO: {}", rno);
            return resultMap;
        } catch (Exception e) {
            log.error("댓글 삭제 중 오류 발생 - RNO: {}", rno, e);
            throw e;
        }
    }@Operation(description = "Modify Reply", summary = "PUT 방식으로 특정 댓글 수정")
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> modify(@PathVariable("rno") Long rno, @RequestBody ReplyDTO replyDTO) {
        log.info("댓글 수정 요청 - RNO: {}, ReplyDTO: {}", rno, replyDTO);
        
        try {
            replyDTO.setRno(rno);
            replyService.modify(replyDTO);
            Map<String, Long> resultMap = new HashMap<>();
            resultMap.put("rno", rno);
            log.info("댓글 수정 성공 - RNO: {}", rno);
            return resultMap;
        } catch (Exception e) {
            log.error("댓글 수정 중 오류 발생 - RNO: {}", rno, e);
            throw e;
        }
    }
}