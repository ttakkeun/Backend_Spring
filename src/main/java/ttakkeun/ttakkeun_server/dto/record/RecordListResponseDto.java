package ttakkeun.ttakkeun_server.dto.record;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.temporal.TemporalQueries.localDate;

@Getter
@Setter
public class RecordListResponseDto {

    private Long recordId;
    private LocalDate updatedAtDate;
    private LocalTime updatedAtTime;

    public RecordListResponseDto(Long recordId, LocalDate localDate, LocalTime localTime) {
        this.recordId = recordId;
        this.updatedAtDate = localDate;
        this.updatedAtTime = localTime;
    }
}
