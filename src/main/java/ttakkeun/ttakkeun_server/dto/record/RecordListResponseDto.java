package ttakkeun.ttakkeun_server.dto.record;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecordListResponseDto {

    private Long recordId;
    private LocalDateTime createdAt;

    public RecordListResponseDto(Long recordId, LocalDateTime createdAt) {
        this.recordId = recordId;
        this.createdAt = createdAt;
    }
}
