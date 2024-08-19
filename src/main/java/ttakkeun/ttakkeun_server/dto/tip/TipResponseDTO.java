package ttakkeun.ttakkeun_server.dto.tip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ttakkeun.ttakkeun_server.entity.enums.Category;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipResponseDTO {
    private Long tipId;
    private Category category;
    private String title;
    private String content;
    private Integer recommendCount;
    private LocalDateTime createdAt;
    private List<String> imageUrls;
    private String authorName;
    private Boolean isPopular;
}
