package site.board.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.board.domain.Board;
import site.board.dto.BoardListDTO;

public interface BoardSearch {
    Page<Board> search(String[] types, String keyword, Pageable pageable);

    Page<BoardListDTO> searchWithImages(String[] types, String keyword, Pageable pageable);
}
