package ttakkeun.ttakkeun_server.dto.tip;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ttakkeun.ttakkeun_server.entity.enums.Category;

@Getter
@Setter
@NoArgsConstructor
public class TipCreateRequestDTO {
    private Long memberId;
    private String title;
    private String content;
    private Category tipCategory;

    public TipCreateRequestDTO(Long memberId, String title, String content, Category tipCategory) {
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.tipCategory = tipCategory;
    }
}