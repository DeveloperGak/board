package site.board.service;

import site.board.domain.Board;
import site.board.dto.BoardDTO;
import site.board.dto.BoardListDTO;
import site.board.dto.PageRequestDTO;
import site.board.dto.PageResponseDTO;

import java.util.List;

public interface BoardService {
    Long register(BoardDTO boardDTO);

    BoardDTO readOne(Long id);

    void modify(BoardDTO boardDTO);

    void delete(Long id);

    PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO);

    PageResponseDTO<BoardListDTO> listWithAll(PageRequestDTO pageRequestDTO);

    default Board dtoToEntity(BoardDTO boardDTO) {
        Board board = Board.builder()
                .id(boardDTO.getId())
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .writer(boardDTO.getWriter())
                .build();

        if(boardDTO.getFileNames() != null) {
            boardDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            });
        }

        return board;
    }

    default BoardDTO entityToDTO(Board board) {
        BoardDTO boardDTO = BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();

        List<String> fileNames = board.getImageSet().stream().sorted().map(
                boardImage -> boardImage.getUuid() + "_" + boardImage.getFileName()).toList();

        boardDTO.setFileNames(fileNames);

        return boardDTO;
    }
}
