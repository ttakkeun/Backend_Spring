package ttakkeun.ttakkeun_server.dto.diagnose;

import java.util.List;

public record ChatGPTRequestDTO(String model, List<ChatGPTCompletionDTO> messages) {
    // model - 사용할 Chat GPT 모델
    // messages - 대화 내역
}