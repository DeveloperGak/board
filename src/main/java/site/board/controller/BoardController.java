package site.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import site.board.dto.BoardDTO;
import site.board.dto.BoardListDTO;
import site.board.dto.PageRequestDTO;
import site.board.dto.PageResponseDTO;
import site.board.service.BoardService;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping("/api/board")
public class BoardController {

    @Value("${site.board.upload.path}")
    private String uploadPath;

    private final BoardService boardService;

    @GetMapping("/list")
    public PageResponseDTO<BoardListDTO> list(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<BoardListDTO> responseDTO = boardService.listWithAll(pageRequestDTO);

        return responseDTO;
    }

    @GetMapping("/{boardId}")
    public BoardDTO read(@PathVariable("boardId") Long boardId) {
        BoardDTO boardDTO = boardService.readOne(boardId);
        log.info(boardDTO);

        return boardDTO;
    }

    @PutMapping(value = "/{boardId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> modify(@PathVariable("boardId") Long boardId, @RequestBody @Valid BoardDTO boardDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return Map.of("result", "error");
        }

        boardDTO.setId(boardId);
        boardService.modify(boardDTO);

        return Map.of("result", "success");
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> registerPOST(@RequestBody @Valid BoardDTO boardDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return Map.of("error", "error");
        }

        Long id = boardService.register(boardDTO);

        return Map.of("id", id.toString());
    }

    @PreAuthorize("principal.username == #boardDTO.writer")
    @DeleteMapping("/{boardId}")
    public Map<String, String> remove(@PathVariable("boardId") Long boardId, BoardDTO boardDTO) {
        List<String> fileNames = boardDTO.getFileNames();
        if(fileNames != null && fileNames.size() > 0){
            removeFiles(fileNames);
        }

        boardService.delete(boardId);

        return Map.of("result", "success");
    }

    public void removeFiles(List<String> files) {
        for (String fileName: files) {
            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();

                if(contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath + File.separator + "thumb" + File.separator + "s_" + fileName);
                    thumbnailFile.delete();
                }
            }
            catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
