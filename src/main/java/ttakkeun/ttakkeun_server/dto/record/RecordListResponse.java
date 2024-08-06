package ttakkeun.ttakkeun_server.dto.record;

import lombok.Getter;
import ttakkeun.ttakkeun_server.entity.enums.Category;

import java.util.List;

@Getter
public class RecordListResponse {

    private Category category;
    private List<RecordListResponseDto> recordList;

    public RecordListResponse(Category category, List<RecordListResponseDto> recordList) {
        this.category = category;
        this.recordList = recordList;
    }

}
