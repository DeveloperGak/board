package site.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardListDTO {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime createdAt;

    private List<BoardImageDTO> boardImages;
}
