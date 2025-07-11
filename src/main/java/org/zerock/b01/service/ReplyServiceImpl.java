package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.zerock.b01.domain.Reply;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReplyDTO;
import org.zerock.b01.repository.BoardRepository;
import org.zerock.b01.repository.ReplyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;
    @Override
    public Long register(ReplyDTO replyDTO) {
        Reply reply = modelMapper.map(replyDTO, Reply.class);
        Long rno = replyRepository.save(reply).getRno();
        return rno;
    }

    @Override
    public ReplyDTO read(Long rno) {
        Optional<Reply> replyOptional = replyRepository.findById(rno);
        Reply reply = replyOptional.orElseThrow();
        return modelMapper.map(reply, ReplyDTO.class);
    }

    @Override
    public void modify(ReplyDTO replyDTO) {
//        Long rno = replyDTO.getRno();
//        if (!replyRepository.existsById(rno)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "리뷰가 존재하지 않습니다.");
//        }
        Optional<Reply> replyOptional = replyRepository.findById(replyDTO.getRno());
        Reply reply = replyOptional.orElseThrow();
        reply.changeText(replyDTO.getReplyText());
        replyRepository.save(reply);
    }

    @Override
    public void remove(Long rno) {
        if (!replyRepository.existsById(rno)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "리뷰가 존재하지 않습니다.");
        }
        replyRepository.deleteById(rno);
    }

    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {
        if (!boardRepository.existsById(bno)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시물이 존재하지 않습니다.");
        }

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <=0 ? 0 : pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("rno").ascending());
        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);
        List<ReplyDTO> dtoList = result.getContent().stream().map(reply -> modelMapper
                .map(reply, ReplyDTO.class)).collect(Collectors.toList());
        return PageResponseDTO.<ReplyDTO>withAll().pageRequestDTO(pageRequestDTO).dtoList(dtoList)
                .total((int)result.getTotalElements()).build();
    }
}
