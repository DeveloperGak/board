package site.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
@ToString
public class CrewDTO extends User {
    private String id;
    private String password;

    public CrewDTO(String username, String password, Collection<GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = username;
        this.password = password;
    }
}
