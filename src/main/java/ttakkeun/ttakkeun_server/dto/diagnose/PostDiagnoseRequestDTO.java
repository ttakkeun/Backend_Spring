package ttakkeun.ttakkeun_server.dto.diagnose;

import java.util.List;

public record PostDiagnoseRequestDTO(Long pet_id, List<Record> records) {

    public static record Record(Long record_id) {
    }
}
