package ttakkeun.ttakkeun_server.dto.todo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RepeatAnotherDayRequestDto {
    private LocalDate newDate;
}
