package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
@ToString(exclude = "roleSet")
public class Member {
    @Id
    private String mid;
    private String mpw;
    private String email;
    private boolean del;
    private boolean social;

    @Column(name = "regdate", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime regdate = LocalDateTime.now(); // 등록일자

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberRole> roleSet = new HashSet<>();

    public void changePassword(String mpw) {
        this.mpw = mpw;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changeDel(boolean del) {
        this.del = del;
    }

    public void addRole(MemberRole memberRole) {
        this.roleSet.add(memberRole);
    }

    public void clearRoles() {
        this.roleSet.clear();
    }

    public void changeSocial(boolean social) {
        this.social = social;
    }

    @PrePersist
    public void onPrePersist() {
        if (this.regdate == null) {
            this.regdate = LocalDateTime.now();  // 현재 시간으로 설정
        }
    }
}
