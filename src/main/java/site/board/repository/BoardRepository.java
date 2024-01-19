package site.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.board.domain.Board;
import site.board.repository.search.BoardSearch;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {
}
