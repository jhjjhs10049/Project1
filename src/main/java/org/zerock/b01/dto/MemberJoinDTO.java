package org.zerock.b01.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberJoinDTO {

    private String mid;
    private String mpw;
    private String email;
    private boolean del;
    private boolean social;
    private LocalDateTime regdate;
}
