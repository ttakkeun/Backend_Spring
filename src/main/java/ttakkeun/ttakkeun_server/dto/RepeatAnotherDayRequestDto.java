package ttakkeun.ttakkeun_server.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RepeatAnotherDayRequestDto {
    private LocalDateTime newDate;
}
