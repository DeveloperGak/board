package site.board.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import site.board.domain.Board;
import site.board.domain.QBoard;
import site.board.dto.BoardImageDTO;
import site.board.dto.BoardListDTO;

import java.util.List;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    public BoardSearchImpl() {
        super(Board.class);
    }

    @Override
    public Page<Board> search(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board);

        if( (types != null && types.length > 0) && keyword != null) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            for(String type: types) {
                switch (type){
                    case "title":
                        booleanBuilder.or(board.title.contains(keyword)); break;
                    case "content":
                        booleanBuilder.or(board.content.contains(keyword)); break;
                    case "writer":
                        booleanBuilder.or(board.writer.contains(keyword)); break;
                }
            }
            query.where(booleanBuilder);
        }
        query.where(board.id.gt(0L));

        this.getQuerydsl().applyPagination(pageable, query);
        List<Board> list = query.fetch();
        long count = query.fetchCount();

        return new PageImpl<>(list, pageable, count);
    }

    @Override
    public Page<BoardListDTO> searchWithImages(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        JPQLQuery<Board> boardJPQLQuery = from(board);

        if( (types != null && types.length > 0) && keyword != null) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            for(String type: types) {
                switch (type) {
                    case "title" -> booleanBuilder.or(board.title.contains(keyword));
                    case "content" -> booleanBuilder.or(board.content.contains(keyword));
                    case "writer" -> booleanBuilder.or(board.writer.contains(keyword));
                }
                boardJPQLQuery.where(booleanBuilder);
            }
        }

        getQuerydsl().applyPagination(pageable, boardJPQLQuery);

        List<Board> list = boardJPQLQuery.fetch();

        List<BoardListDTO> dtoList = list.stream().map(board1 -> {

            BoardListDTO dto = BoardListDTO.builder()
                    .id(board1.getId())
                    .title(board1.getTitle())
                    .writer(board1.getWriter())
                    .content(board1.getContent())
                    .createdAt(board1.getCreatedAt())
                    .build();

            List<BoardImageDTO> imageDTOS = board1.getImageSet().stream().sorted()
                    .map(boardImage -> BoardImageDTO.builder()
                            .uuid(boardImage.getUuid())
                            .fileName(boardImage.getFileName())
                            .ord(boardImage.getOrd())
                            .build()
                    ).toList();

            dto.setBoardImages(imageDTOS);

            return dto;
        }).toList();

        long totalCount = boardJPQLQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }
}
