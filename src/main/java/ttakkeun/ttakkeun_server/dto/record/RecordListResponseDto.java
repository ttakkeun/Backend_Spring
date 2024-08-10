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
    private LocalDate createdAtDate;
    private LocalTime createdAtTime;

    public RecordListResponseDto(Long recordId, LocalDate localDate, LocalTime localTime) {
        this.recordId = recordId;
        this.createdAtDate = localDate;
        this.createdAtTime = localTime;
    }
}
