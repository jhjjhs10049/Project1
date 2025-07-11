package org.zerock.b01.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
public class MemberSecurityDTO extends User implements OAuth2User {

    private String mid;

    private String mpw;

    private String email;

    private boolean del;

    private boolean social;
    
    private LocalDateTime regdate;

    private Map<String, Object> props;    public MemberSecurityDTO(String username, String password, String email, boolean del, boolean social,
                             LocalDateTime regdate, Collection<? extends GrantedAuthority> authorities){
        super(username, password, authorities);

        this.mid = username;
        this.mpw = password;
        this.email = email;
        this.del = del;
        this.social = social;
        this.regdate = regdate;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.getProps();
    }

    @Override
    public String getName() {
        return this.mid;
    }
}
