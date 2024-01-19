package site.board.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String writer;

    @Column(length = 3000, nullable = false)
    private String content;

    private boolean isDeleted;

    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void changeIsDeleted(Boolean deleted) {
        this.isDeleted = deleted;
    }

    @BatchSize(size = 20)
    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<BoardImage> imageSet = new HashSet<>();

    public void addImage(String uuid, String fileName) {
        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();

        imageSet.add(boardImage);
    }

    public void clearImages() {
        imageSet.forEach(boardImage -> boardImage.changeBoard(null));
        this.imageSet.clear();
    }
}
