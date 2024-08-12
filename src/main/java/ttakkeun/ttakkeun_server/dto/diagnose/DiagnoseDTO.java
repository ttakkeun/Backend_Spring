package ttakkeun.ttakkeun_server.dto.diagnose;

import java.time.LocalDateTime;

public record DiagnoseDTO(Long diagnose_id, LocalDateTime created_at, Integer score) {
}
