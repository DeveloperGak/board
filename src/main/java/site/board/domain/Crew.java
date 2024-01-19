package site.board.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Crew {

    @Id
    private String id;
    private String password;
    private CrewRole role;

    public void changePassword(String password) {
        this.password = password;
    }
    public void changeRole(CrewRole crewRole) {this.role = crewRole;}
}
