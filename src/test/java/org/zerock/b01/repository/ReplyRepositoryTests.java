package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.Reply;
import org.zerock.b01.dto.BoardListReplyCountDTO;
import org.zerock.b01.dto.ReplyDTO;
import org.zerock.b01.service.ReplyService;

@SpringBootTest
@Log4j2
public class ReplyRepositoryTests {
    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private ReplyService replyService;

    @Test
    public void testInsert() {
        Long bno = 100L;
        Board board = Board.builder().bno(bno).build();
        Reply reply = Reply.builder().board(board).replyText("댓글....")
                .replier("replier1").build();
        replyRepository.save(reply);
    }

    @Test
    public void testBoardReplies() {
        Long bno = 100L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("rno").descending());
        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);
        result.getContent().forEach(log::info);
    }

    @Test
    public void testRegister() {
        ReplyDTO replyDTO = ReplyDTO.builder().replyText("ReplyDTO Text")
                .replier("replier").bno(100L).build();
        log.info(replyService.register(replyDTO));
    }
}
