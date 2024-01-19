package site.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int take = 10;

    private String type;
    private String keyword;
    private String link;
    private LocalDate from;
    private LocalDate to;
    private Boolean deleted;

    public String[] getTypes() {
        if(type == null || type.isEmpty()) {
            return null;
        }

        return type.split(",");
    }

    public Pageable getPageable(String ...props) {
        return PageRequest.of(this.page -1, this.take, Sort.by(props).descending());
    }

    public String getLink() {
        if (link == null ) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("page=").append(this.page);
            stringBuilder.append("&size=").append(this.take);

            if(type != null && type.length() > 0) {
                stringBuilder.append("&type=").append(type);
            }

            if(keyword != null) {
                try {
                    stringBuilder.append("&keyword=").append(URLEncoder.encode(keyword, "UTF-8"));
                }
                catch (UnsupportedEncodingException e){}
            }
            link = stringBuilder.toString();
        }

        return link;
    }
}
