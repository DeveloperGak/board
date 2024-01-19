package site.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PageResponseDTO<E> {
    private int page;
    private int take;
    private int totalCount;

    private int start;
    private int end;

    private boolean prev;
    private boolean next;

    private List<E> data;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> data, int totalCount) {
        if(totalCount <= 0) return;

        this.page = pageRequestDTO.getPage();
        this.take = pageRequestDTO.getTake();
        this.totalCount = totalCount;
        this.end = (int) Math.ceil(this.page / (float)this.take) * this.take;
        this.start = this.end - (this.take - 1);
        this.data = data;

        int last = (int)Math.ceil((totalCount/(double)take));

        this.end = Math.min(end, last);
        this.prev = this.start > 1;
        this.next = totalCount > this.end * this.take;
    }
}
