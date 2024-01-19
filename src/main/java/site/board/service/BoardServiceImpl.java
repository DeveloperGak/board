package site.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.board.domain.Board;
import site.board.dto.BoardDTO;
import site.board.dto.BoardListDTO;
import site.board.dto.PageRequestDTO;
import site.board.dto.PageResponseDTO;
import site.board.repository.BoardRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
@Transactional
@Service
public class BoardServiceImpl implements BoardService {

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;

    @Override
    public Long register(BoardDTO boardDTO) {
        Board board = dtoToEntity(boardDTO);

        return boardRepository.save(board).getId();
    }

    @Override
    public BoardDTO readOne(Long id) {
        Optional<Board> result = boardRepository.findById(id);
        Board board = result.orElseThrow();

        return entityToDTO(board);
    }

    @Override
    public void modify(BoardDTO boardDTO) {
        Optional<Board> result = boardRepository.findById(boardDTO.getId());
        Board board = result.orElseThrow();
        board.change(boardDTO.getTitle(), boardDTO.getContent());

        if(boardDTO.getFileNames() != null) {
            board.clearImages();
            for (String fileName : boardDTO.getFileNames()) {
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            }
        }

        boardRepository.save(board);
    }

    @Override
    public void delete(Long id) {
        Optional<Board> result = boardRepository.findById(id);
        Board board = result.orElseThrow();
        board.changeIsDeleted(true);

        boardRepository.save(board);
    }

    @Override
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("id");

        Page<Board> result = boardRepository.search(types, keyword, pageable);

        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class)).toList();

        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .data(dtoList)
                .totalCount((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<BoardListDTO> listWithAll(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("id");
        Page<BoardListDTO> result = boardRepository.searchWithImages(types, keyword, pageable);

        return PageResponseDTO.<BoardListDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .data(result.getContent())
                .totalCount((int) result.getTotalElements())
                .build();
    }
}
