package ttakkeun.ttakkeun_server.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

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
