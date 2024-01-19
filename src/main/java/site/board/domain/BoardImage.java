package site.board.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BoardImage implements Comparable<BoardImage> {

    @Id
    private String uuid;
    private String fileName;
    private int ord;

    @ToString.Exclude
    @ManyToOne
    private Board board;

    @Override
    public int compareTo(BoardImage o) {
        return this.ord - o.ord;
    }

    public void changeBoard(Board board) {
        this.board = board;
    }
}
