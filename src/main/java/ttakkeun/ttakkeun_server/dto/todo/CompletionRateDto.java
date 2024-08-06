package ttakkeun.ttakkeun_server.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompletionRateDto {
    private int earTotal;
    private int earCompleted;
    private int hairTotal;
    private int hairCompleted;
    private int clawTotal;
    private int clawCompleted;
    private int eyeTotal;
    private int eyeCompleted;
    private int teethTotal;
    private int teethCompleted;
}
